package io.wasin.asteriods.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import io.wasin.asteriods.Game
import io.wasin.asteriods.entities.Asteriod
import io.wasin.asteriods.entities.AsteriodPool
import io.wasin.asteriods.entities.Player
import io.wasin.asteriods.handlers.BBInput
import io.wasin.asteriods.handlers.GameStateManager

/**
 * Created by haxpor on 6/16/17.
 */
class Play(gsm: GameStateManager): GameState(gsm){

    private var sr: ShapeRenderer = ShapeRenderer()
    private var player: Player = Player(4)

    lateinit private var asteriodPool: AsteriodPool
    private var asteriods: ArrayList<Asteriod> = ArrayList()

    private var level: Int = 1
    private var totalAsteriods: Int = 0
    private var numAsteriodsLeft: Int = 0

    companion object {
        const val SAFE_SPAWN_DIST: Float = 100f
    }

    init {
        spawnAsteriods()
    }

    override fun handleInput(dt: Float) {
        // left button is pressed and player is not going left
        if (BBInput.isDown(BBInput.BUTTON_LEFT) && !player.left) {
            player.left = true
        }
        // left button is pressed and player is not going left
        else if (!BBInput.isDown(BBInput.BUTTON_LEFT) && player.left) {
            player.left = false
        }

        // right button is pressed and player is not going right
        if (BBInput.isDown(BBInput.BUTTON_RIGHT) && !player.right) {
            player.right = true
        }
        // right button is pressed and player is not going right
        else if (!BBInput.isDown(BBInput.BUTTON_RIGHT) && player.right) {
            player.right = false
        }

        // up button is pressed and player is not going up
        if (BBInput.isDown(BBInput.BUTTON_UP) && !player.up) {
            player.up = true
        }
        // up button is pressed and player is not going up
        else if (!BBInput.isDown(BBInput.BUTTON_UP) && player.up) {
            player.up = false
        }

        // shoot
        if (BBInput.isPressed(BBInput.BUTTON_SPACE)) {
            player.shoot()
        }
    }

    override fun update(dt: Float) {
        handleInput(dt)

        player.update(dt)

        if (asteriods.count() > 0) {
            for (i in asteriods.count()-1 downTo 0) {
                val a = asteriods[i]
                a.update(dt)
                if (a.shouldBeRemoved) {
                    asteriods.removeAt(i)
                    asteriodPool.free(a)
                }
            }
        }
    }

    override fun render() {
        // clear screen
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // render player
        player.render(sr)

        // batch render for asteriods
        sr.begin(ShapeRenderer.ShapeType.Line)
        for (a in asteriods) {
            a.renderBatch(sr)
        }
        sr.end()
    }

    override fun dispose() {

    }

    override fun resize_user(width: Int, height: Int) {
    }

    private fun spawnAsteriods() {
        var numToSpawn = level + 3  // aim to spawn only large asteriods
        totalAsteriods = numToSpawn * 7 // as bigger asteriod can split into 2 asteriod, for all hierarchy for its types
        numAsteriodsLeft = totalAsteriods

        // create asteriod pool match total number of asteriods to have in such level
        asteriodPool = AsteriodPool(totalAsteriods)

        var x: Float
        var y: Float

        // spawn large asteriods outside of the safe area
        for (n in 1..numToSpawn) {
            do {
                x = MathUtils.random(Game.V_WIDTH)
                y = MathUtils.random(Game.V_HEIGHT)

                val dx = x - player.x
                val dy = y - player.y
                val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
            } while(dist < SAFE_SPAWN_DIST)

            // get free object from the pool
            val a = asteriodPool.obtain()
            // spawn
            a.spawn(x, y, Asteriod.Type.LARGE)
            // also add into our active list of asteriods
            asteriods.add(a)
        }
    }
}