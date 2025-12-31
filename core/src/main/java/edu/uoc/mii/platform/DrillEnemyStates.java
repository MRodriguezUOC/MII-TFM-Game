package edu.uoc.mii.platform;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;

/**
 *
 * @author Marco Rodriguez
 */
public enum DrillEnemyStates implements State<DrillEnemy> {

    DRILLING() {
        @Override
        public void update(DrillEnemy e) {
            e.drilling();
            if (e.playerDistance() < DrillEnemy.MIN_PLAYER_DIST) {
                e.getStateMachine().changeState(DrillEnemyStates.SCRUMLEADER);
            }

            if (MathUtils.randomBoolean(0.01f)) {
                e.getStateMachine().changeState(DrillEnemyStates.RESTING);
            }
        }
        
        @Override
        public boolean onMessage(DrillEnemy e, Telegram telegram) {
            if (telegram.message == DrillMessage.MSG_SCRUM_ENTER) {
                e.scrumLeader = (DrillEnemy) telegram.sender;
                e.getStateMachine().changeState(DrillEnemyStates.PROTECTING);

                return true;
            }
            return false;
        }        
    },
    RESTING() {
        @Override
        public void enter(DrillEnemy e) {
            e.stopMove();
            e.resetTimer();
        }

        @Override
        public void update(DrillEnemy e) {
            e.updateTimer();

            if (e.getTimer() > MathUtils.random(0.5f, 3.0f)) {
                e.getStateMachine().changeState(DrillEnemyStates.DRILLING);
            }

            if (e.playerDistance() < DrillEnemy.MIN_PLAYER_DIST) {
                e.getStateMachine().changeState(DrillEnemyStates.SCRUMLEADER);
            }
        }
        
        @Override
        public boolean onMessage(DrillEnemy e, Telegram telegram) {
            if (telegram.message == DrillMessage.MSG_SCRUM_ENTER) {
                e.scrumLeader = (DrillEnemy) telegram.sender;
                e.getStateMachine().changeState(DrillEnemyStates.PROTECTING);

                return true;
            }
            return false;
        }         
    },
    PROTECTING() {
        @Override
        public void update(DrillEnemy e) {
            if(e.scrumLeader == null){
                e.getStateMachine().changeState(DrillEnemyStates.DRILLING);
            }else{
                e.protecting();
            }
        }
        
        @Override
        public boolean onMessage(DrillEnemy e, Telegram telegram) {
            if (telegram.message == DrillMessage.MSG_SCRUM_ENTER) {
                if(!telegram.sender.equals(e.scrumLeader)){
                    e.scrumLeader = (DrillEnemy) telegram.sender;
                }
                return true;
            }else if(telegram.message == DrillMessage.MSG_SCRUM_LEAVE){
                if(telegram.sender.equals(e.scrumLeader)){
                    e.scrumLeader = null;
                    e.getStateMachine().changeState(DrillEnemyStates.DRILLING);
                }
                return true;
            }
            return false;
        }         
    },
    SCRUMLEADER() {
        @Override
        public void enter(DrillEnemy e) {
            // TODO: play sound?
            MessageManager.getInstance().dispatchMessage(0.0f,
                    e,
                    null,
                    DrillMessage.MSG_SCRUM_ENTER,
                    e.playerDistance()
            );
        }

        @Override
        public void update(DrillEnemy e) {
            e.escape();
            if (e.playerDistance() > DrillEnemy.MIN2_PLAYER_DIST) {
                e.getStateMachine().changeState(DrillEnemyStates.DRILLING);
                MessageManager.getInstance().dispatchMessage(0.0f,
                    e,
                    null,
                    DrillMessage.MSG_SCRUM_LEAVE,
                    null
                );
            }
        }
        
        @Override
        public boolean onMessage(DrillEnemy e, Telegram telegram) {
            if (telegram.message == DrillMessage.MSG_SCRUM_ENTER) {
                if(e.playerDistance() > (float) telegram.extraInfo){
                    e.scrumLeader = (DrillEnemy) telegram.sender;
                    e.getStateMachine().changeState(DrillEnemyStates.PROTECTING);
                }
                return true;
            }
            return false;
        }         
    };

    @Override
    public void enter(DrillEnemy entity) {
    }

    @Override
    public void exit(DrillEnemy entity) {
    }
}
