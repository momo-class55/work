# Local smoke check: validate compose and start stack (requires Docker Desktop / Engine).
$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "..")
if (-not (Test-Path ".env")) {
    Write-Error "Missing .env — copy .env.example to .env and set POSTGRES_PASSWORD (and other vars)."
}
docker compose config | Out-Null
docker compose up -d --build
docker compose ps
