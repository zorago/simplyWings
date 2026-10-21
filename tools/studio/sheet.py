import sys
from pathlib import Path
from PIL import Image, ImageDraw

shots = Path(sys.argv[1])
out = Path(sys.argv[2])
names = sys.argv[3:]
crop = 0.62
tiles = []
for name in names:
    image = Image.open(shots / f"{name}.png").convert("RGB")
    w, h = image.size
    cw, ch = int(w * crop), int(h * crop)
    box = ((w - cw) // 2, (h - ch) // 2, (w + cw) // 2, (h + ch) // 2)
    tile = image.crop(box).resize((cw * 480 // cw, ch * 480 // cw))
    ImageDraw.Draw(tile).text((8, 8), name, fill=(255, 255, 255))
    tiles.append(tile)
cols = min(3, len(tiles))
rows = (len(tiles) + cols - 1) // cols
tw, th = tiles[0].size
sheet = Image.new("RGB", (cols * tw, rows * th), (20, 20, 20))
for i, tile in enumerate(tiles):
    sheet.paste(tile, ((i % cols) * tw, (i // cols) * th))
out.parent.mkdir(parents=True, exist_ok=True)
sheet.save(out)
print(out)
