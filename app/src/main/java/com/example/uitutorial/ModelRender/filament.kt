package com.example.uitutorial.ModelRender

import android.content.Context
import android.util.Log
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.filament.*
import com.google.android.filament.gltfio.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.google.android.filament.gltfio.UbershaderLoader

class FilamentView(context: Context): SurfaceView(context), SurfaceHolder.Callback, Choreographer.FrameCallback{
    private lateinit var engine: Engine
    private lateinit var renderer: Renderer
    private lateinit var scene: Scene
    private lateinit var view: View
    private lateinit var swapChain: SwapChain
    private lateinit var camera: Camera
    private lateinit var entityManager: EntityManager

    private lateinit var assetLoader: AssetLoader
    private lateinit var resourceLoader: ResourceLoader
    private var filamentAsset: FilamentAsset? = null

    private var choreographer: Choreographer? = null
    private var initialized = false
    private var rotationAngle: Float = 0f

    private val TAG = "FilamentView"

    init {
        holder.addCallback(this)
        System.loadLibrary("filament-jni")
        System.loadLibrary("gltfio-jni")
    }

    private fun rotateModel(angleDegrees: Float) {
        filamentAsset?.let { asset ->
            val transformManager = engine.transformManager
            val root = asset.root
            val transformInstance = transformManager.getInstance(root)

            if (transformInstance != 0) {
                val rotationMatrix = FloatArray(16)
                android.opengl.Matrix.setRotateM(rotationMatrix, 0, angleDegrees, 1f, 1f, 0f) // Rotate around Y-axis

                transformManager.setTransform(transformInstance, rotationMatrix)
            }
        }
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            engine = Engine.create()
            renderer = engine.createRenderer()
            swapChain = engine.createSwapChain(holder.surface)
            scene = engine.createScene()
            view = engine.createView()
            entityManager = EntityManager.get()
            val materialProvider = UbershaderLoader(engine)

            assetLoader = AssetLoader(engine, materialProvider, entityManager)
            resourceLoader = ResourceLoader(engine)

            val cameraEntity = entityManager.create()
            camera = engine.createCamera(cameraEntity)
            camera.setProjection(45.0, width.toDouble() / height.toDouble(), 0.1, 100.0, Camera.Fov.HORIZONTAL)
            camera.lookAt(0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
            view.camera = camera
            view.scene = scene
            setupLighting()
            loadGlbModel("model.glb")

            startRenderLoop()

            initialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Error during Filament initialization: ${e.message}", e)
        }
    }

    private fun setupLighting() {
        val light = entityManager.create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1.0f, 1.0f, 1.0f)
            .intensity(100000.0f)
            .direction(-0.5f, -0.8f, -0.2f)
            .castShadows(true)
            .build(engine, light)
        scene.addEntity(light)
    }

    private fun loadGlbModel(fileName: String) {
        try {
            val inputStream = context.assets.open(fileName)
            val bytes = inputStream.readBytes()
            inputStream.close()

            val buffer = ByteBuffer.allocateDirect(bytes.size).order(ByteOrder.nativeOrder()).put(bytes).flip()
            filamentAsset = assetLoader.createAssetFromBinary(buffer)

            if (filamentAsset != null) {
                resourceLoader.asyncBeginLoad(filamentAsset!!)
                scene.addEntities(filamentAsset!!.entities)
            } else {
                Log.e(TAG, "Failed to load GLB asset")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading GLB model: ${e.message}", e)
        }
    }

    private fun startRenderLoop() {
        choreographer = Choreographer.getInstance()
        choreographer?.postFrameCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        view.viewport = Viewport(0, 0, width, height)
        camera.setProjection(45.0, width.toDouble() / height.toDouble(), 0.1, 100.0, Camera.Fov.HORIZONTAL)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        choreographer?.removeFrameCallback(this)
        choreographer = null
        engine.destroy()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (initialized) {
            rotationAngle += 0.1f
            rotateModel(rotationAngle)

            if (filamentAsset != null) {
                Log.d(TAG, "GLB model resources are still loading")
            }

            if (renderer.beginFrame(swapChain, frameTimeNanos)) {
                renderer.render(view)
                renderer.endFrame()
            }
            choreographer?.postFrameCallback(this)
            scene.skybox = Skybox.Builder().color(0.0f, 0.0f, 0.0f, 1.0f).build(engine)

        }
    }
}

@Composable
fun FilamentComposeView() {
    AndroidView(
        factory = { context -> FilamentView(context) },
        modifier = Modifier.fillMaxSize()
    )
}
