export function create(root, ctx) {
  root.innerHTML = `
    <div class="view-head"><strong>Boucle de bloc</strong>
      <button data-a="play">▶ en boucle</button><button data-a="stop">■</button>
      <label>vitesse <input data-a="speed" type="range" min="0" max="1" step="0.01" value="1"></label>
      <span class="hint" data-a="info"></span></div>
    <div class="view-body" style="padding:10px"><p class="hint">Le compresseur module le volume et la hauteur selon son avancement (CloudCompressorEffects) : le curseur reproduit cette modulation (volume 0,25 + 0,35 × vitesse, hauteur 0,85 + 0,25 × vitesse). Écoute au moins 10 minutes avant de valider.</p></div>`;
  let entry = null;
  let handle = null;
  const speed = root.querySelector('[data-a="speed"]');
  const apply = () => {
    if (!handle) return;
    const s = parseFloat(speed.value);
    handle.gainNode.gain.value = (0.25 + 0.35 * s) * (handle.leaf?.volume ?? 1);
    handle.src.playbackRate.value = (0.85 + 0.25 * s) * (handle.leaf?.pitch ?? 1);
  };
  speed.oninput = apply;
  root.querySelector('[data-a="play"]').onclick = async () => {
    handle?.stop();
    handle = await ctx.playEvent("simplywings:" + entry.id, { loop: true, distance: ctx.state.distance });
    apply();
  };
  root.querySelector('[data-a="stop"]').onclick = () => {
    handle?.stop();
    handle = null;
  };
  return {
    show(e) {
      entry = e;
      handle?.stop();
      handle = null;
      root.querySelector('[data-a="info"]').textContent = e.id;
    },
    hide() {
      handle?.stop();
    },
  };
}
