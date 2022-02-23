package daylightnebula.mobagamegdx.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import daylightnebula.mobagame.network.*
import daylightnebula.mobagame.network.playerstuffs.GameClass
import daylightnebula.mobagamegdx.MobaGame
import daylightnebula.mobagamegdx.items.Item

class ItemSelectionScene: Scene() {

    private lateinit var stage: Stage
    private lateinit var font: BitmapFont

    // labels
    private lateinit var timeLabel: TextField
    private lateinit var primeLabel: TextField
    private lateinit var secondLabel: TextField
    private lateinit var armorLabel: TextField

    // styles
    private lateinit var labelStyle: TextField.TextFieldStyle
    private lateinit var notSelectedStyle: TextButton.TextButtonStyle
    private lateinit var selectedStyle: TextButton.TextButtonStyle
    private lateinit var readyStyle: TextButton.TextButtonStyle

    // buttons
    private lateinit var readyButton: TextButton
    private val primeButtons = mutableListOf<TextButton>()
    private val secondButtons = mutableListOf<TextButton>()
    private val armorButtons = mutableListOf<TextButton>()

    private var timeLeft = 45
    private var isReady = false

    override fun create() {
        // in opengl context
        Gdx.app.postRunnable {
            // create ui
            stage = Stage()
            Gdx.input.inputProcessor = stage
            font = BitmapFont()

            // create styles
            labelStyle = TextField.TextFieldStyle(font, Color(1f, 1f, 1f, 1f), null, null, null)
            notSelectedStyle = TextButton.TextButtonStyle()
            notSelectedStyle.font = font
            notSelectedStyle.fontColor = Color(0.75f, 0.75f, 0.75f, 1f)
            selectedStyle = TextButton.TextButtonStyle()
            selectedStyle.font = font
            selectedStyle.fontColor = Color(0.7f, 1f, 0.7f, 1f)
            readyStyle = TextButton.TextButtonStyle()
            readyStyle.font = font
            readyStyle.fontColor = Color(1f, 0.7f, 0.7f, 1f)

            // create labels
            timeLabel = TextField(timeLeft.toString(), labelStyle)
            primeLabel = TextField("Primary Items", labelStyle)
            secondLabel = TextField("Secondary Items", labelStyle)
            armorLabel = TextField("Armor Items", labelStyle)
            stage.addActor(timeLabel)
            stage.addActor(primeLabel)
            stage.addActor(secondLabel)
            stage.addActor(armorLabel)

            // create ready button
            readyButton = TextButton("Ready", notSelectedStyle)
            readyButton.addListener {
                if (it !is ChangeListener.ChangeEvent) return@addListener true
                if (!isReady && MobaGame.primeItem != -1 && MobaGame.secondItem != -1 && MobaGame.armorItem != -1) {
                    readyButton.style = readyStyle
                    MobaGame.game.connection.sendPacket(
                        IReadyPacket(MobaGame.matchID, MobaGame.userID)
                    )
                    isReady = true
                }
                true
            }
            stage.addActor(readyButton)

            // create buttons
            val myClass = GameClass.list.firstOrNull { it.classID == MobaGame.currentClass }
            println("My class ${myClass?.name ?: "null"}")
            myClass?.primeItems?.forEach { itemID ->
                val item = Item.items.firstOrNull { it.id == itemID }
                println("Item found ${item?.name ?: "null"}")
                if (item != null) {
                    val button = TextButton(item.name, notSelectedStyle)
                    primeButtons.add(button)
                    button.addListener {
                        if (it !is ChangeListener.ChangeEvent) return@addListener true
                        // reset last class if another was selected before
                        if (MobaGame.primeItem != -1)
                            primeButtons[MobaGame.primeItem].style = notSelectedStyle

                        // set style
                        MobaGame.primeItem = item.id
                        button.style = selectedStyle

                        // send packet
                        MobaGame.game.connection.sendPacket(
                            IBuyItemPacket(MobaGame.matchID, MobaGame.userID, item.id, 0)
                        )

                        true
                    }
                    stage.addActor(button)
                }
            }
            myClass?.secondItems?.forEach { itemID ->
                val item = Item.items.firstOrNull { it.id == itemID }
                if (item != null) {
                    val button = TextButton(item.name, notSelectedStyle)
                    secondButtons.add(button)
                    button.addListener {
                        if (it !is ChangeListener.ChangeEvent) return@addListener true
                        // reset last class if another was selected before
                        if (MobaGame.secondItem != -1)
                            secondButtons[MobaGame.secondItem].style = notSelectedStyle

                        // set style
                        MobaGame.secondItem = item.id
                        button.style = selectedStyle

                        // send packet
                        MobaGame.game.connection.sendPacket(
                            IBuyItemPacket(MobaGame.matchID, MobaGame.userID, item.id, 1)
                        )

                        true
                    }
                    stage.addActor(button)
                }
            }
            myClass?.armorItems?.forEach { itemID ->
                val item = Item.items.firstOrNull { it.id == itemID }
                if (item != null) {
                    val button = TextButton(item.name, notSelectedStyle)
                    armorButtons.add(button)
                    button.addListener {
                        if (it !is ChangeListener.ChangeEvent) return@addListener true
                        // reset last class if another was selected before
                        if (MobaGame.armorItem != -1)
                            armorButtons[MobaGame.armorItem].style = notSelectedStyle

                        // set style
                        MobaGame.armorItem = item.id
                        button.style = selectedStyle

                        // send packet
                        MobaGame.game.connection.sendPacket(
                            IBuyItemPacket(MobaGame.matchID, MobaGame.userID, item.id, 2)
                        )

                        true
                    }
                    stage.addActor(button)
                }
            }

            // finalize
            positionUI(Gdx.graphics.width, Gdx.graphics.height)
        }
    }

    private fun positionUI(width: Int, height: Int) {
        if (!this::selectedStyle.isInitialized) return
        timeLabel.setPosition(width / 2f, height - 20f, Align.center)
        primeLabel.setPosition(width / 4f, height - 40f, Align.center)
        secondLabel.setPosition(width / 2f, height - 40f, Align.center)
        armorLabel.setPosition(width * 0.75f, height - 40f, Align.center)
        readyButton.setPosition(width / 2f, 20f, Align.center)

        // update buttons
        primeButtons.forEachIndexed { index, button ->
            button.setPosition(
                width / 4f,
                height - 60f - (20f * index),
                Align.center
            )
        }
        secondButtons.forEachIndexed { index, button ->
            button.setPosition(
                width / 2f,
                height - 60f - (20f * index),
                Align.center
            )
        }
        armorButtons.forEachIndexed { index, button ->
            button.setPosition(
                width * 0.75f,
                height - 60f - (20f * index),
                Align.center
            )
        }
    }

    override fun resize(width: Int, height: Int) {
        positionUI(width, height)
    }

    override fun render() {
        if (!this::stage.isInitialized) return

        // setup render
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        stage.draw()
    }

    override fun dispose() {
        Gdx.input.inputProcessor = null
    }

    override fun processServerPacket(serverPacket: ServerPacket): Boolean {
        if (serverPacket is ConnectToOpponentPacket) {
            Gdx.app.postRunnable {
                MobaGame.game.changeScene(GameScene(if (serverPacket.isServer) "" else serverPacket.address, serverPacket.port))
            }
            return true
        } else if (serverPacket is TimeLeftPacket) {
            timeLeft = serverPacket.timeLeft
            if (this::timeLabel.isInitialized) timeLabel.text = timeLeft.toString()
            return true
        }
        return false
    }
}