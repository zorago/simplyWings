# Sound Bench

Banc local pour écouter, mesurer, régler et valider les sons de Simply Wings à côté de l'animation qui les déclenche (`SOUND_BENCH.md`).

```
cd tools/sound-bench
npm install                      # première fois ; bench.config.json se crée depuis bench.config.example.json
npm run dev                      # http://localhost:5177
```

Prérequis : Node 20+, Python (pour `build_sounds.py`), ffmpeg et les assets de Minecraft 1.21.1 (`minecraftAssets` dans `bench.config.json`). Les poses des ailes viennent de `./gradlew bakeBench` (lancé tout seul au démarrage si le bake est périmé).
