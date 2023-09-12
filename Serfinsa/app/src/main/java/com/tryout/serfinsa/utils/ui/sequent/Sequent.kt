package com.tryout.serfinsa.utils.ui.sequent

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import java.util.*


class Sequent private constructor(builder: Builder) {
    private val viewList: MutableList<View?> = ArrayList()
    private val startOffset: Int
    private val duration: Int
    private val delay: Int
    private val direction: Direction
    private val context: Context?
    private val animId: Int
    private val anim: Animation?

    class Builder internal constructor(val vg: ViewGroup) {
        var startOffset = DEFAULT_OFFSET
        var duration = DEFAULT_DURATION
        var delay = DEFAULT_DURATION
        var direction = Direction.FORWARD
        var context: Context? = null
        var animId = 0
        var anim: Animation? = null
        fun offset(offset: Int): Builder {
            startOffset = offset
            return this
        }

        fun duration(duration: Int): Builder {
            this.duration = duration
            return this
        }

        fun delay(delay: Int): Builder {
            this.delay = delay
            return this
        }

        fun flow(direction: Direction): Builder {
            this.direction = direction
            return this
        }

        fun anim(context: Context?, animId: Int): Builder {
            this.context = context
            this.animId = animId
            return this
        }

        fun anim(context: Context?, anim: Animation?): Builder {
            this.context = context
            this.anim = anim
            return this
        }

        fun start(): Sequent {
            return Sequent(this)
        }

        companion object {
            private const val DEFAULT_OFFSET = 80
            private const val DEFAULT_DURATION = 400
            private const val DEFAULT_DELAY = 0
        }
    }

    init {
        startOffset = builder.startOffset
        duration = builder.duration
        delay = builder.delay
        direction = builder.direction
        context = builder.context
        animId = builder.animId
        anim = builder.anim
        val vg = builder.vg
        fetchChildLayouts(vg)
        arrangeLayouts(viewList)
        setAnimation()
    }

    private fun fetchChildLayouts(viewGroup: ViewGroup) {
        val count = viewGroup.childCount
        for (i in 0 until count) {
            val view = viewGroup.getChildAt(i)
            if (view is ViewGroup) {
                fetchChildLayouts(view)
            } else {
                if (view.visibility == View.VISIBLE) {
                    view.visibility = View.INVISIBLE
                    viewList.add(view)
                }
            }
        }
    }

    private fun arrangeLayouts(viewList: List<View?>): List<View?> {
        when (direction) {
            Direction.BACKWARD -> Collections.reverse(viewList)
            Direction.RANDOM -> Collections.shuffle(viewList)
            else -> {}
        }
        return viewList
    }

    private fun setAnimation() {
        val count = viewList.size
        for (i in 0 until count) {
            val view = viewList[i]
            val offset = i * startOffset
            resetAnimation(view)
            val animatorList: MutableList<Animator> = ArrayList()
            animatorList.add(getStartObjectAnimator(offset, view))
            if (animId != 0) {
                animatorList.add(getResAnimator(context, animId, view))
            } else if (anim != null) {
                animatorList.add(getResAnimator(context, anim.animId, view))
            } else {
                animatorList.add(ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f))
            }
            val set = AnimatorSet()
            set.playTogether(animatorList)
            set.duration = duration.toLong()
            if (delay == 0) {
                set.startDelay = (i * startOffset).toLong()
            } else if (i == 0) {
                set.startDelay = delay.toLong()
            } else {
                set.startDelay = (i * startOffset + delay).toLong()
            }
            set.start()
        }
    }

    private fun resetAnimation(view: View?) {
        ViewCompat.setAlpha(view, 1f)
        ViewCompat.setScaleX(view, 1f)
        ViewCompat.setScaleY(view, 1f)
        ViewCompat.setTranslationX(view, 0f)
        ViewCompat.setTranslationY(view, 0f)
        ViewCompat.setRotation(view, 0f)
        ViewCompat.setRotationY(view, 0f)
        ViewCompat.setRotationX(view, 0f)
    }

    private fun getStartObjectAnimator(offset: Int, view: View?): ObjectAnimator {
        val ob = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f)
        ob.setDuration(1).startDelay = offset.toLong()
        ob.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(anim: Animator) {
                view!!.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(anim: Animator) {}
            override fun onAnimationEnd(anim: Animator) {}
            override fun onAnimationCancel(anim: Animator) {}
        })
        return ob
    }

    private fun getResAnimator(context: Context?, animId: Int, view: View?): Animator {
        val anim = AnimatorInflater.loadAnimator(context, animId)
        anim.setTarget(view)
        return anim
    }

    companion object {
        fun origin(vg: ViewGroup): Builder {
            return Builder(vg)
        }
    }
}