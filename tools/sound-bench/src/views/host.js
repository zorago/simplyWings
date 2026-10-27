import { playEvent, stopAll } from "../audio.js";

const loaders = {
  wings: () => import("./wings.js"),
  gacha: () => import("./gacha.js"),
  loop: () => import("./loop.js"),
};

export function kindOf(e) {
  if (e.category === "gacha" || e.group === "cards" || e.id === "ui.confirm") return "gacha";
  if (e.loop && (e.category === "block" || e.id === "flight.wind" || e.category === "wing")) return e.category === "block" ? "loop" : "wings";
  if (e.category === "wing" || e.category === "flight" || e.category === "fury") return "wings";
  return null;
}

export function createViewHost(root, ctx) {
  const views = new Map();
  let current = null;
  const helpers = { ...ctx, playEvent, stopAll };
  return {
    async show(e) {
      const kind = kindOf(e);
      if (current && current.kind !== kind) {
        current.view.hide?.();
        current.el.style.display = "none";
        current = null;
      }
      if (!kind) {
        root.querySelector(".empty")?.remove();
        root.insertAdjacentHTML("beforeend", `<p class="hint empty" style="padding:10px">Pas de vue pour ce son : écoute-le avec ▶ Jeu, ou rejoue la séquence réelle en jeu (/simplywings sound ${e.id}).</p>`);
        return;
      }
      root.querySelector(".empty")?.remove();
      if (!views.has(kind)) {
        const el = document.createElement("div");
        el.style.cssText = "display:flex;flex-direction:column;flex:1;min-height:0";
        root.appendChild(el);
        const mod = await loaders[kind]();
        views.set(kind, { kind, el, view: mod.create(el, helpers) });
      }
      current = views.get(kind);
      current.el.style.display = "flex";
      current.view.show(e);
    },
  };
}
