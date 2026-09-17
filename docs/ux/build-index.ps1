$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot '../..')).Path
$sourceRoot = Join-Path $repoRoot 'telegram-bot/src/main/java/cs/youtrade/autotrade/client'
$sourceFiles = Get-ChildItem -LiteralPath $sourceRoot -Recurse -Filter '*.java' | Sort-Object FullName
$texts = @{}
$handlers = @{}
foreach ($sourceFile in $sourceFiles) {
    $content = Get-Content -LiteralPath $sourceFile.FullName -Raw -Encoding UTF8
    $texts[$sourceFile.FullName] = $content
    $match = [regex]::Match($content, 'UserMenu supportedState\(\)\s*\{\s*return UserMenu\.([A-Z0-9_]+)')
    if ($match.Success) { $handlers[$match.Groups[1].Value] = $sourceFile }
}
$enumText = $texts[(Join-Path $sourceRoot 'telegram/menu/UserMenu.java').Replace('/', '\')]
$states = [regex]::Matches($enumText.Split(@('private int cmdId'), [StringSplitOptions]::None)[0], '(?m)^    ([A-Z][A-Z0-9_]*)(?:\(|[,;])') | ForEach-Object { $_.Groups[1].Value }
function SourceLink($file) {
    $relative = $file.FullName.Substring($repoRoot.Length + 1).Replace('\', '/')
    return ('[' + $file.Name + '](../../' + $relative + ')')
}
$lines = [System.Collections.Generic.List[string]]::new()
$lines.Add('# Static repository and interface index')
$lines.Add('')
$lines.Add('Snapshot: 2026-09-17. Regenerate: `powershell -File docs/ux/build-index.ps1`.')
$lines.Add('')
$lines.Add('Static declarations only; state references include error paths and self references, not just navigation. No live bot or backend calls.')
$lines.Add('')
$lines.Add(('Java files: {0}; UserMenu states: {1}; explicit supportedState handlers: {2}.' -f $sourceFiles.Count, $states.Count, $handlers.Count))
$lines.Add('')
$lines.Add('| State | Explicit handler |')
$lines.Add('|---|---|')
foreach ($state in $states) {
    $handler = if ($handlers.ContainsKey($state)) { SourceLink $handlers[$state] } else { 'Not found in this repository' }
    $lines.Add('| `' + $state + '` | ' + $handler + ' |')
}
$lines.Add('')
$lines.Add('## Menu labels and state references')
foreach ($file in $sourceFiles) {
    if ($file.FullName -notmatch '\\telegram\\menu\\' -or $file.Name -notmatch '(State|Menu|Notifier)\.java$') { continue }
    $content = $texts[$file.FullName]
    $labels = [regex]::Matches($content, '\b[A-Z][A-Z0-9_]*\(\s*"([^"\r\n]+)"') | ForEach-Object { $_.Groups[1].Value }
    $references = [regex]::Matches($content, 'UserMenu\.([A-Z0-9_]+)') | ForEach-Object { $_.Groups[1].Value } | Sort-Object -Unique
    $lines.Add('')
    $lines.Add('### ' + $file.BaseName)
    $lines.Add('')
    $lines.Add((SourceLink $file))
    $lines.Add('')
    if ($labels) { $lines.Add('Labels: ' + ($labels -join '; ')); $lines.Add('') }
    if ($references) { $lines.Add('State references: ' + ($references -join ', ')); $lines.Add('') }
}
$lines.Add('## Source inventory')
$lines.Add('')
foreach ($file in $sourceFiles) {
    $lines.Add('- ' + (SourceLink $file) + ' - `' + $file.FullName.Substring($sourceRoot.Length + 1).Replace('\', '/') + '`')
}
$lines.Add('')
$lines.Add('## Resources')
$lines.Add('')
Get-ChildItem -LiteralPath (Join-Path $repoRoot 'telegram-bot/src/main/resources') -Recurse -File | Sort-Object FullName | ForEach-Object { $lines.Add('- ' + (SourceLink $_)) }
[System.IO.File]::WriteAllLines((Join-Path $PSScriptRoot 'repository-index.md'), $lines, [System.Text.UTF8Encoding]::new($false))
Write-Output ('Indexed {0} Java files, {1} states, {2} explicit handlers.' -f $sourceFiles.Count, $states.Count, $handlers.Count)
$states | Where-Object { -not $handlers.ContainsKey($_) } | ForEach-Object { Write-Output ('No explicit handler: ' + $_) }
