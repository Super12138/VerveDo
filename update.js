"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const child_process_1 = require("child_process");
const fs_1 = require("fs");
(0, child_process_1.exec)("git rev-list --count HEAD", (err, versionCode, stderr) => {
    if (err) {
        console.log(err);
        return;
    }
    const filePath = "./app/build.gradle.kts";
    try {
        const data = (0, fs_1.readFileSync)(filePath, "utf-8");
        const regex = /(?<=versionCode\s*=\s*)\d+/;
        const oldVerionCode = data.match(regex);
        const newString = data.replace(regex, versionCode.trim());
        try {
            (0, fs_1.writeFileSync)(filePath, newString);
            console.log(`版本号更新完成，已由 ${oldVerionCode} 更新至 ${versionCode}`);
            process.exit(0);
        }
        catch (e) {
            console.log(e);
        }
    }
    catch (e) {
        console.log(e);
    }
});
