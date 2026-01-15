(function () {
  const STORAGE_KEY = "rg-theme";

  function getPreferredTheme() {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored === "light" || stored === "dark") return stored;
    return window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches
      ? "dark"
      : "light";
  }

  function applyTheme(theme) {
    document.documentElement.dataset.theme = theme;
    const btns = document.querySelectorAll("[data-theme-toggle]");
    btns.forEach(btn => {
      btn.setAttribute("aria-pressed", theme === "dark" ? "true" : "false");
      // Mettre un libellé clair sur tous les boutons (remplace l'emoji si présent)
      btn.innerText = theme === "dark" ? "Thème Clair" : "Thème Sombre";
    });
  }

  function toggleTheme() {
    const current = document.documentElement.dataset.theme || getPreferredTheme();
    const next = current === "dark" ? "light" : "dark";
    localStorage.setItem(STORAGE_KEY, next);
    applyTheme(next);
  }

  applyTheme(getPreferredTheme());

  window.addEventListener("DOMContentLoaded", () => {
    const btns = document.querySelectorAll("[data-theme-toggle]");
    btns.forEach(btn => btn.addEventListener("click", toggleTheme));
  });

  window.RunGeniusTheme = { applyTheme, toggleTheme };
  // Menu hamburger mobile
  window.addEventListener("DOMContentLoaded", () => {
    const hamburger = document.querySelector('.rg-hamburger');
    const mobileMenu = document.getElementById('rg-mobile-menu');
    const closeBtn = document.querySelector('.rg-mobile-menu-close');
    if (hamburger && mobileMenu) {
      hamburger.addEventListener('click', () => {
        mobileMenu.setAttribute('aria-hidden', 'false');
        hamburger.setAttribute('aria-expanded', 'true');
      });
    }
    if (closeBtn && mobileMenu && hamburger) {
      closeBtn.addEventListener('click', () => {
        mobileMenu.setAttribute('aria-hidden', 'true');
        hamburger.setAttribute('aria-expanded', 'false');
      });
    }
    // Fermer le menu mobile si on clique en dehors
    document.addEventListener('click', (e) => {
      if (mobileMenu && hamburger && mobileMenu.getAttribute('aria-hidden') === 'false') {
        if (!mobileMenu.contains(e.target) && !hamburger.contains(e.target)) {
          mobileMenu.setAttribute('aria-hidden', 'true');
          hamburger.setAttribute('aria-expanded', 'false');
        }
      }
    });
  });
})();