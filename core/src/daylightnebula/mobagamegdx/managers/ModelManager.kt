package daylightnebula.mobagamegdx.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.utils.UBJsonReader
import net.mgsx.gltf.loaders.gltf.GLTFLoader

class ModelManager {
    companion object {
        //private lateinit var modelLoader: G3dModelLoader
        //private lateinit var gltfModelLoader: GLTFLoader
        private val modelMap = hashMapOf<String, Model>() // format: name, model

        fun getModel(name: String): Model {
            // try to get the model from the model map
            var model = modelMap[name]

            // if no model is in the map, try to get one from the files
            if (model == null) {
                println("Model $name not loaded, loading...")
                // create model loader if needed
                val gltfModelLoader = GLTFLoader()

                // get data from file
                val fileHandle = Gdx.files.local("$name.gltf")
                val sceneasset = gltfModelLoader.load(fileHandle, true)

                // get and store model
                model = sceneasset.scene.model
                modelMap[name] = model
            } else
                println("Model $name loaded")

            // finalize
            return model!!
        }

        fun dispose() {
            modelMap.values.forEach {
                it.dispose()
            }
        }
    }
}