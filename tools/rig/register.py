import re
import sys
from pathlib import Path

name = sys.argv[1]
path = Path(__file__).resolve().parents[2] / "src/main/java/com/zorago/simplywings/render/rig/WingRigs.java"
s = path.read_text(encoding="utf-8")
imp = f"import com.zorago.simplywings.render.rig.wings.{name};\n"
if imp not in s:
    imports = sorted(set(re.findall(r"import com\.zorago\.simplywings\.render\.rig\.wings\.\w+;\n", s)) | {imp})
    s = re.sub(r"(import com\.zorago\.simplywings\.render\.rig\.wings\.\w+;\n)+", "".join(imports), s, count=1)
    s = re.sub(r"(            new \w+\(\))\);", rf"\1,\n            new {name}());", s, count=1)
    path.write_text(s, encoding="utf-8")
