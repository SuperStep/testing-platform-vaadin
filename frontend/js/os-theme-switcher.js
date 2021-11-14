function applyTheme() {
    let theme = matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
    document.documentElement.setAttribute("theme", theme);
}
matchMedia("(prefers-color-scheme: dark)").addEventListener("change", applyTheme);
// Uncomment if you want to sync theme with OS
// applyTheme();