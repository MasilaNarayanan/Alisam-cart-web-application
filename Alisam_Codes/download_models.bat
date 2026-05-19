@echo off
mkdir "src\main\resources\static\models" 2>nul
curl -s -L -o "src\main\resources\static\models\mug.glb" "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/WaterBottle/glTF-Binary/WaterBottle.glb"
curl -s -L -o "src\main\resources\static\models\tshirt.glb" "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Corset/glTF-Binary/Corset.glb"
curl -s -L -o "src\main\resources\static\models\cover.glb" "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/BoxTextured/glTF-Binary/BoxTextured.glb"
curl -s -L -o "src\main\resources\static\models\lantern.glb" "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Lantern/glTF-Binary/Lantern.glb"
curl -s -L -o "src\main\resources\static\models\shoe.glb" "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Shoe/glTF-Binary/Shoe.glb"
echo Download complete!
