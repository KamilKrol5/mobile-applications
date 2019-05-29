package com.example.tictactoe


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.choose_mode_framgent.view.*


class ChooseModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.choose_mode_framgent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.buttonOnline.setOnClickListener { onlineGameChosen() }
        view.buttonThisPhone.setOnClickListener { localGameChosen() }
    }

    private fun localGameChosen() {
        val gameFragment = GameFragment()
        requireFragmentManager().beginTransaction().replace(R.id.mainLayout, gameFragment, "game")
            .addToBackStack("chooseGame")
            .commit()
    }

    private fun onlineGameChosen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
