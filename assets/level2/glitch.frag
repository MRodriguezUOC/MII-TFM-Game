#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_time;

// CORRECCIÓN AQUÍ: Usamos float en lugar de boolean
uniform float u_active; 

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    // CORRECCIÓN AQUÍ: Comparamos si es mayor que 0.5 (es decir, si es 1.0)
    if (u_active < 0.5) {
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    } else {
        vec2 uv = v_texCoords;
        
        // ... resto del código del glitch igual ...
        float wave = sin(uv.y * 50.0 + u_time * 10.0) * 0.005;
        float noise = rand(vec2(u_time, uv.y));
        
        if (noise > 0.9) {
            uv.x += (rand(vec2(u_time, uv.y)) - 0.5) * 0.1;
        }
        
        uv.x += wave;
        vec4 color = texture2D(u_texture, uv);
        
        if (rand(vec2(u_time, uv.y)) > 0.95) {
            color.r = 0.0; 
            color.g = 1.0; 
        }
        
        gl_FragColor = v_color * color;
    }
}