package com.enzab.spootify.service.interaction;

import com.enzab.spootify.model.Song;

/**
 * Created by linard_f on 5/5/16.
 */
public interface OnCompletionViewListener {

    void updateView(Song song);

}
