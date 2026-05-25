# start-dev.ps1
# Levanta el back (Spring Boot) y el front (Next.js con pnpm) en terminales separadas

$backDir  = $PSScriptRoot
$frontDir = "..\subsistemaSeguridadFront"

Write-Host "=== PALA Seguridad - Iniciando entorno de desarrollo ===" -ForegroundColor Cyan

function Free-Port($Port) {
    $processIds = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
    if ($processIds) {
        Write-Host "Liberando puerto $Port..." -ForegroundColor Yellow
        foreach ($procId in $processIds) {
            Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
        }
        Start-Sleep -Seconds 1
    }
}

Free-Port 8080
Free-Port 3000

# --- Backend: Spring Boot via Gradle ---
Write-Host "[BACK] Iniciando Spring Boot en $backDir ..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$backDir'; Write-Host '[BACK] Spring Boot' -ForegroundColor Yellow; .\gradlew.bat bootRun"
)

# --- Frontend: Next.js via pnpm ---
$frontAbsolute = Resolve-Path (Join-Path $backDir $frontDir)
Write-Host "[FRONT] Iniciando Next.js en $frontAbsolute ..." -ForegroundColor Green
Start-Process powershell -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$frontAbsolute'; Write-Host '[FRONT] Next.js (pnpm)' -ForegroundColor Green; pnpm dev"
)

Write-Host ""
Write-Host "Ambos servidores iniciados en ventanas separadas." -ForegroundColor Cyan
Write-Host "  Back  -> http://localhost:8080" -ForegroundColor Yellow
Write-Host "  Front -> http://localhost:3000" -ForegroundColor Green
