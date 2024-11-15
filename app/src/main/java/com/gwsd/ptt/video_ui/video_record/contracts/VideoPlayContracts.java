package com.gwsd.ptt.video_ui.video_record.contracts;

import com.gwsd.ptt.video_ui.video_record.help.EmptyControlVideo;

/**
 * Created by Nicky on 2017/12/15.
 */

public interface VideoPlayContracts {

    interface VideoPlayUI{
        void uiPlay(String path);
        void uiStopPlay();
        void uiAddCallbackPlayTimer(EmptyControlVideo.CallbackPlayTimer callbackPlayTimer);
    }


}
