package com.yjl.funk.widget.lrc;

import java.util.List;

/**
 * Created by tonyy on 2017/11/18.
 */

public class LrcFinder {

    public static int findShowLine(List<LrcEntry> mLrcEntryList, long time) {
        int left = 0;
        int right = mLrcEntryList.size();
        while (left <= right) {
            int middle = (left + right) / 2;
            long middleTime = mLrcEntryList.get(middle).getTime();

            if (time < middleTime) {
                right = middle - 1;
            } else {
                if (middle + 1 >= mLrcEntryList.size() || time < mLrcEntryList.get(middle + 1).getTime()) {
                    return middle;
                }

                left = middle + 1;
            }
        }

        return 0;
    }


}
