# Momentum Blade

Adds a **Throwing Knife** to Minecraft 1.20.1 (Fabric). The knife deals more damage the farther
it flies before hitting a target — a point-blank hit does modest damage, while a long-range throw
across the battlefield lands a devastating blow.

## How it works

- Craft the Throwing Knife (2 iron ingots stacked over a stick, yields 4 knives).
- **Right-click** to throw. The knife instantly travels in the direction you are looking.
- Damage scales with distance: base **3** damage, plus **0.5** per block travelled, up to a
  maximum range of **32 blocks** (~19 damage at full range).
- Each throw consumes one knife and applies a short cooldown.

---

## Building the mod with GitHub (no Java install needed!)

You do **not** need to install Java or Gradle. GitHub will build the `.jar` for you for free.

1. **Create a GitHub account** at https://github.com (if you don't have one).
2. Click the **+** in the top-right → **New repository**. Give it any name and create it.
3. On the new empty repo page, click the **"uploading an existing file"** link.
4. **Extract the downloaded zip** of this project on your computer.
5. **On macOS:** the `.github` folder is **hidden by default**. In Finder, press
   **Cmd + Shift + .** (period) to reveal hidden files. ⚠️ **If you skip this step, the
   `.github` folder will not be uploaded, the build workflow will never run, and you will
   never get a `.jar`.**
6. Open the extracted folder, **select ALL files and folders INSIDE it** — including the
   hidden `.github` folder — and **drag them into the GitHub upload area**.
   - Drag the **contents** of the folder, **not** the folder itself.
7. Scroll down and click **Commit changes**.
8. Click the **Actions** tab at the top of your repo. You'll see the build running.
9. Wait about **2 minutes** for it to finish (green checkmark).
10. Click the completed run → scroll to **Artifacts** → download **mod-jar**.
11. Unzip it and copy the `.jar` into your `.minecraft/mods/` folder.
    - Make sure you also have [Fabric Loader](https://fabricmc.net/use/installer/) and the
      [Fabric API](https://modrinth.com/mod/fabric-api) installed for Minecraft 1.20.1.

Launch Minecraft with the Fabric profile and enjoy your Momentum Blade!

## License

MIT