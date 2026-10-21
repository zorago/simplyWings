#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
for m in "$@"; do
  node ../../.claude/skills/pxl-pixel-art/bin/pxl.js build "$m.px.js" | tail -1
  cp "out/sprites/${m}_wings.png" "../../src/main/resources/assets/simplywings/textures/entity/${m}_wings.png"
  if [ -f "out/sprites/${m}_glow.png" ]; then cp "out/sprites/${m}_glow.png" "../../src/main/resources/assets/simplywings/textures/entity/${m}_glow.png"; fi
  node -e "global.window={};const fs=require('fs');eval(['lib/rig_pack.js','lib/bird.js','$m.rig.js'].map(f=>fs.readFileSync(f,'utf8')).join('\n')+';const n=RIG.cubes.length;console.log(\"$m: \"+n+\" cubes per side, uv rows used \"+RIG.used+\"/\"+(RIG.texture[1]-16))')"
done
