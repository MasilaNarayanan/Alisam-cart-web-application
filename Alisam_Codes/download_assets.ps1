$modelsDir = "src\main\resources\static\models"
$imagesDir = "src\main\resources\static\images\products"

# Create directories if they don't exist
New-Item -ItemType Directory -Force -Path $modelsDir | Out-Null
New-Item -ItemType Directory -Force -Path $imagesDir | Out-Null

$assets = @(
    # Models
    @{ Url = "https://modelviewer.dev/shared-assets/models/Chair.glb"; Path = "$modelsDir\mug.glb" },
    @{ Url = "https://modelviewer.dev/shared-assets/models/NeilArmstrong.glb"; Path = "$modelsDir\magicMug.glb" },
    @{ Url = "https://modelviewer.dev/shared-assets/models/RobotExpressive.glb"; Path = "$modelsDir\tshirt.glb" },
    @{ Url = "https://modelviewer.dev/shared-assets/models/shishkebab.glb"; Path = "$modelsDir\hoodie.glb" },
    @{ Url = "https://modelviewer.dev/shared-assets/models/Astronaut.glb"; Path = "$modelsDir\mobileCover.glb" },

    # Images
    @{ Url = "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?q=80&w=600"; Path = "$imagesDir\mug.jpg" },
    @{ Url = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?q=80&w=600"; Path = "$imagesDir\tshirt.jpg" },
    @{ Url = "https://images.unsplash.com/photo-1556821840-3a63f95609a7?q=80&w=600"; Path = "$imagesDir\hoodie.jpg" },
    @{ Url = "https://images.unsplash.com/photo-1572635196237-14b3f281503f?q=80&w=600"; Path = "$imagesDir\mobileCover.jpg" }
)

foreach ($asset in $assets) {
    Write-Host "Downloading $($asset.Url)..."
    try {
        Invoke-WebRequest -Uri $asset.Url -OutFile $asset.Path -UseBasicParsing
        Write-Host " Saved to $($asset.Path)"
    } catch {
        Write-Host " Failed to download $($asset.Url)"
    }
}

Write-Host "Download Complete!"
