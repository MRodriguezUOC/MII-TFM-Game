package edu.uoc.mii.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import edu.uoc.mii.conf.PreferenceManager;

/**
 *
 * @author Marco Rodriguez
 */
public class PlatformMap implements Disposable {

    private final TiledMap map;
    // TODO: put private??
    public final int tileWidth;
    public final int tileHeight;
    public final int worldWidth;
    public final int worldHeight;
    private final TiledMapTileLayer floor;
    private final TiledMapTileLayer wall;
    private final MapLayer objects;

    private final Array<Rectangle> upFloors;
    private final Array<Rectangle> invisibleFloors;
    private final Array<Rectangle> iceFloors;
    private final Array<Rectangle> autoFloors;
    private final Array<Rectangle> deadZones;
    private final Array<PointMapObject> enemiesStart;
    private Vector2 startPoint;
    private Vector2 checkPoint;
    private Vector2 wallToBroke;
    private Vector2 door;
    private Vector2 pushButton;
    private Vector2 transporter;
    private Rectangle checkPointArea;
    private Rectangle finishArea;
    private Rectangle autoFloor;
    private Rectangle pushButtonAction;
    private Rectangle doorBounds;

    public boolean doorOpen = false;

//    private final float unitScale = 1 / 1;
    public final float GRAVITY;
    public final float FRICTION;
    public final float MAX_VELOCITY_X;
    public final float MAX_VELOCITY_Y;

    public PlatformMap(TiledMap map) {
        this.map = map;
        //TODO: read from map?	
        GRAVITY = -450f;
        FRICTION = 1f;
        MAX_VELOCITY_X = 300f;
        MAX_VELOCITY_Y = -350f;

        floor = (TiledMapTileLayer) map.getLayers().get("Floor");
        wall = (TiledMapTileLayer) map.getLayers().get("Wall");
        tileWidth = floor.getTileWidth();
        tileHeight = floor.getTileHeight();
        worldWidth = tileWidth * floor.getWidth();
        worldHeight = tileHeight * floor.getHeight();
        Gdx.app.log("PlatformMap", "tileWidth: " + tileWidth + ", tileHeight: " + tileHeight + ", worldWidth: " + worldWidth + ", worldHeight: " + worldHeight);

        upFloors = new Array<>();
        invisibleFloors = new Array<>();
        iceFloors = new Array<>();
        autoFloors = new Array<>();
        deadZones = new Array<>();
        enemiesStart = new Array<>();
        objects = map.getLayers().get("Objects");
        parseMapObjects(objects);
    }

    public float checkWallCollision(Rectangle rect, boolean right) {
        float x = rect.x;
        float y = rect.y;
        float w = rect.width;
        float h = rect.height;

        float correction = 0;

        if (right) {
            if (isCellBlocked(x + w, y, wall)
                    || isCellBlocked(x + w, y + h - 1, wall)
                    || (!doorOpen && rect.overlaps(doorBounds))) {
                correction = (float) (Math.floor((x + w) / tileWidth) * tileWidth) - w;
            }
        } else {
            if (isCellBlocked(x, y, wall)
                    || isCellBlocked(x, y + h - 1, wall)
                    || (!doorOpen && rect.overlaps(doorBounds))) {
                correction = (float) (Math.floor(x / tileWidth) + 1) * tileWidth;
            }
        }

        return correction;
    }

    public JumpCorrection checkFloorCollision(Rectangle rect, boolean jumping) {
        float x = rect.x;
        float y = rect.y;
        float w = rect.width;
        float h = rect.height;

        boolean inTransporter = inTransporter(rect);
        if (inTransporter) {
            y = (float) (Math.floor(transporter.y / tileHeight) * tileHeight) + tileHeight * 2;
            return new JumpCorrection(y, 0);
        }

        if (jumping) {
            y += h - 1;
        } else {
            Rectangle specialFloor = inSpecialFloor(rect);
            if (specialFloor != null) {
                Gdx.app.log("PlatformMap", "inSpecialFloor, floor: " + specialFloor.toString());
                return new JumpCorrection(specialFloor.y + specialFloor.height, 0);
            }
        }

        boolean leftColision = isCellBlocked(x, y, floor);
        boolean rightColision = isCellBlocked(x + w - 1, y, floor);

        if (leftColision || rightColision) {
            float dy = 0f;
            if (jumping && inSpecialZone(rect, upFloors)) {
                y = 0;
                dy = 256 / tileHeight;
            } else if (jumping) {
                y = (float) (((Math.floor(y / tileHeight)) * tileHeight) - tileHeight - 1);
            } else {
                y = (float) (Math.floor(y / tileHeight) + 1) * tileHeight;
            }
            return new JumpCorrection(y, dy);
        } else if (jumping && inSpecialZone(rect, upFloors)) {
            return new JumpCorrection(0, 512 / tileHeight);
        }

        return JumpCorrection.NONE;
    }

    private Rectangle inSpecialFloor(Rectangle rect) {
        for (Rectangle iFloor : invisibleFloors) {
            if (rect.overlaps(iFloor)) {
                return iFloor;
            }
        }

        return null;
    }

    private boolean isCellBlocked(float x, float y, TiledMapTileLayer layer) {
        int cellX = (int) (x / tileWidth);
        int cellY = (int) (y / tileHeight);

        TiledMapTileLayer.Cell cell = layer.getCell(cellX, cellY);

        return cell != null && cell.getTile() != null;
    }

    public boolean isCellBlocked(float x, float y) {
        return isCellBlocked(x, y, floor);
    }
    
    private void parseMapObjects(MapLayer layer) {
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String name = object.getName();

                if (name == null) {
                    Gdx.app.log("PlatformMap", "Objecto whithout name: " + object.toString());
                    continue;
                }

                switch (name) {
                    case "IceFloor":
                        iceFloors.add(rect);
                        break;
                    case "AutoFloor":
                        autoFloors.add(rect);
                        break;
                    case "UpFloor":
                        upFloors.add(rect);
                        break;
                    case "InvisibleFloor":
                        invisibleFloors.add(rect);
                        break;
                    case "Fall":
                    case "EndMap":
                        deadZones.add(rect);
                        break;
                    case "FinishArea":
                        finishArea = rect;
                        break;
                    case "CheckpointArea":
                        checkPointArea = rect;
                        break;
                    case "PushButtonAction":
                        pushButtonAction = rect;
                        break;
                    case "DoorBounds":
                        doorBounds = rect;
                        break;
                    default:
                        Gdx.app.log("PlatformMap", "Ignored RectangleMapObject: " + name);
                }
            } else if (object instanceof PointMapObject) {
                Vector2 point = ((PointMapObject) object).getPoint();
                String name = object.getName();

                if (name == null) {
                    Gdx.app.log("PlatformMap", "Objecto whithout name: " + object.toString());
                    continue;
                }

                switch (name) {
                    case "Start":
                        startPoint = point;
                        float x = PreferenceManager.getInstance().getLevel2StartX();
                        float y = PreferenceManager.getInstance().getLevel2StartY();
                        if (x >= 0) {
                            startPoint.x = x;
                        }
                        if (y >= 0) {
                            startPoint.y = y;
                        }
                        break;
                    case "Checkpoint":
                        checkPoint = point;
                        break;
                    case "WallToBroke":
                        wallToBroke = point;
                        break;
                    case "Door":
                        door = point;
                        break;
                    case "PushButton":
                        pushButton = point;
                        break;
                    case "FinishFloor":
                        transporter = point;
                        break;
                    case "Enemy1":
                    case "Enemy2":
                    case "Enemy3":
                        enemiesStart.add((PointMapObject) object);
                        break;
                    default:
                        Gdx.app.log("PlatformMap", "Ignored PointMapObject: " + name);
                }
            } else {
                Gdx.app.log("PlatformMap", "Unknow object type: " + object.getClass().toString());
            }
        }
    }

    private boolean inSpecialZone(Rectangle rect, Array<Rectangle> zones) {
        boolean inZone = false;
        for (Rectangle zone : zones) {
            if (rect.overlaps(zone)) {
                inZone = true;
                break;
            }
        }
        return inZone;
    }

    public boolean inDeadZone(Rectangle rect) {
        return inSpecialZone(rect, deadZones);
    }

    public boolean inUpFloor(Rectangle rect) {
        return inSpecialZone(rect, upFloors);
    }

    public boolean inIceFloor(Rectangle rect) {
        return inSpecialZone(rect, iceFloors);
    }

    public boolean inTransporter(Rectangle rect) {
        return inSpecialZone(rect, autoFloors);
    }

    public boolean inPushButtonAction(Rectangle rect) {
        return rect.overlaps(pushButtonAction);
    }

    @Override
    public void dispose() {
        map.dispose();
    }

    public Array<PointMapObject> getEnemiesStart() {
        return enemiesStart;
    }

    public Vector2 getStartPoint() {
        return startPoint;
    }

    public Vector2 getCheckPoint() {
        return checkPoint;
    }

    public TiledMap getTiledMap() {
        return map;
    }

    public Array<Rectangle> getUpFloors() {
        return upFloors;
    }

    public Array<Rectangle> getIceFloors() {
        return iceFloors;
    }

    public Array<Rectangle> getDeadZones() {
        return deadZones;
    }

    public Rectangle getFinishArea() {
        return finishArea;
    }

    public Rectangle getCheckPointArea() {
        return checkPointArea;
    }

    public Vector2 getTransporter() {
        return transporter;
    }

    public Vector2 getDoor() {
        return door;
    }

    public Vector2 getPushButton() {
        return pushButton;
    }

    public Rectangle pushButtonAction() {
        return pushButtonAction;
    }

}
