import json, re, sys, zipfile, pathlib, collections

MODS = pathlib.Path(sys.argv[1])
TARGETS = {
    "Camera": b"net/minecraft/client/Camera",
    "MouseHandler": b"net/minecraft/client/MouseHandler",
    "GameRenderer": b"net/minecraft/client/renderer/GameRenderer",
    "LocalPlayer": b"net/minecraft/client/player/LocalPlayer",
    "PlayerRenderer": b"net/minecraft/client/renderer/entity/player/PlayerRenderer",
    "PlayerModel": b"net/minecraft/client/model/PlayerModel",
    "HumanoidModel": b"net/minecraft/client/model/HumanoidModel",
    "LivingEntityRenderer": b"net/minecraft/client/renderer/entity/LivingEntityRenderer",
    "ServerGamePacketListenerImpl": b"net/minecraft/server/network/ServerGamePacketListenerImpl",
    "EndDragonFight": b"net/minecraft/world/level/dimension/end/EndDragonFight",
    "InventoryScreen": b"net/minecraft/client/gui/screens/inventory/InventoryScreen",
    "KeyboardInput": b"net/minecraft/client/player/KeyboardInput",
    "Options/CameraType": b"net/minecraft/client/CameraType",
}
ANN = re.compile(rb"Lorg/spongepowered/asm/mixin/Mixin;")

result = collections.defaultdict(lambda: collections.defaultdict(set))
for jar in sorted(MODS.glob("*.jar")):
    try:
        z = zipfile.ZipFile(jar)
    except Exception:
        continue
    names = z.namelist()
    cfgs = [n for n in names if n.endswith(".json") and "mixin" in n.lower() and "/" not in n.strip("/") or (n.endswith("mixins.json"))]
    pkgs = set()
    for c in cfgs:
        try:
            d = json.loads(z.read(c))
        except Exception:
            continue
        if isinstance(d, dict) and "package" in d:
            pkgs.add(d["package"].replace(".", "/") + "/")
    for n in names:
        if not n.endswith(".class") or not any(n.startswith(p) for p in pkgs):
            continue
        data = z.read(n)
        if not ANN.search(data):
            continue
        for label, t in TARGETS.items():
            if (b"L" + t + b";") in data:
                result[label][jar.name].add(n.rsplit("/", 1)[-1][:-6])

for label in TARGETS:
    print(f"## {label}")
    for jar, cls in sorted(result[label].items()):
        print(f"  {jar}: {', '.join(sorted(cls))}")
