#!/usr/bin/env bash
root="$(cd "$(dirname "$0")/../.." && pwd)"
studio="$root/run/studio"
log="${STUDIO_LOG:-$root/build/studio.log}"
if [ -f "$studio/pid" ] && grep -q "mark ready" "$studio/log.txt" 2>/dev/null; then
  echo "quit" > "$studio/commands.txt"
  for i in $(seq 1 60); do
    if ! grep -q "mark ready" "$studio/log.txt" 2>/dev/null || [ -f "$studio/stopped" ]; then break; fi
    sleep 1
    if ! powershell -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" | Where-Object { \$_.CommandLine -match 'simplywings.studio' -or \$_.CommandLine -match 'devlaunch' } | Select-Object -First 1" | grep -q .; then break; fi
  done
fi
rm -rf "$studio"
mkdir -p "$studio"
cat > "$studio/commands.txt" <<CMDS
cmd gamerule doDaylightCycle false
cmd time set 6000
cmd weather clear
cmd tp @s 0 140 0 0 0
wait 20
begin
mark ready
CMDS
(cd "$root" && ./gradlew --no-configuration-cache runStudio > "$log" 2>&1 &)
touch "$studio/pid"
for i in $(seq 1 600); do
  if grep -q "mark ready" "$studio/log.txt" 2>/dev/null; then echo "studio ready"; exit 0; fi
  if grep -q "BUILD FAILED\|FAILURE:" "$log" 2>/dev/null; then tail -30 "$log"; exit 1; fi
  sleep 1
done
echo "studio start timeout" >&2
exit 1
