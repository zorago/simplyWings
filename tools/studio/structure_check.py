import random
import subprocess
import sys
from pathlib import Path

import nbtlib

sys.stdout.reconfigure(encoding="utf-8")
root = Path(__file__).resolve().parents[2]
SEND = [sys.executable, "-I", str(root / "tools" / "studio" / "send.py")]
CENTER = (28, 18, 28)
STRUCTURES = {"sky_sanctum": (212, 81, -466), "wing_forge": (466, 81, 212), "cloud_refinery": (-212, 81, 466),
              "summoning_sanctum": (0, 223, 0), "offering_fountain": (-466, 81, -212)}


def state(entry):
    name = str(entry["Name"])
    props = entry.get("Properties")
    if not props:
        return name
    return name + "[" + ",".join(f"{k}={v}" for k, v in sorted(props.items())) + "]"


def send(cmds):
    return subprocess.run(SEND + [";".join(cmds)], capture_output=True, text=True, encoding="utf-8").stdout.splitlines()


def check(name, center, samples):
    t = nbtlib.load(root / "src/main/resources/data/simplywings/structure" / f"{name}.nbt")
    palette = [state(p) for p in t["palette"]]
    blocks = [b for b in t["blocks"] if "Properties" in t["palette"][int(b["state"])]]
    random.seed(7)
    picks = random.sample(blocks, min(samples, len(blocks)))
    ox, oy, oz = (center[0] - CENTER[0], center[1] - 1 - CENTER[1], center[2] - CENTER[2])
    send([f"cmd execute in minecraft:the_end run tp @s {center[0]} {center[1] + 20} {center[2]}", "wait 80"])
    cmds = []
    for b in picks:
        x, y, z = (int(v) for v in b["pos"])
        cmds.append(f"cmd execute in minecraft:the_end if block {ox + x} {oy + y} {oz + z} {palette[int(b['state'])]}")
    out = send(cmds + ["wait 10"])
    passed = sum(1 for l in out if "Test passed" in l)
    failed = [l for l in out if "Test failed" in l]
    print(f"{name}: {len(picks)} stated blocks sampled, {passed} passed, {len(failed)} failed")
    for b, l in zip(picks, out):
        pass
    fails = []
    for b in picks:
        x, y, z = (int(v) for v in b["pos"])
        fails.append((ox + x, oy + y, oz + z, palette[int(b['state'])]))
    return passed, failed, fails


if __name__ == "__main__":
    n = int(sys.argv[1]) if len(sys.argv) > 1 else 120
    for name, center in STRUCTURES.items():
        passed, failed, picks = check(name, center, n)
        if failed:
            for line in failed[:5]:
                print("   ", line)
