package synergy.cs530.ccsu.mobileauthentication.enums;

import synergy.cs530.ccsu.mobileauthentication.R;

/**
 * Created by ejwint on 10/22/15.
 */
public enum NotificationEnum {

    TRY_AGAIN(R.string.notification_try_again),
    SUCCESS(R.string.notification_success),
    FAIL(R.string.notification_fail),
    MIS_MATCH(R.string.notification_mis_match),
    RESETTING_SEQUENCES(R.string.notification_resetting_sequences),
    EXPORT_SEQUENCE_SUCCESS(R.string.notification_export_success),
    EXPORT_SEQUENCE_FAIL(R.string.notification_export_fail),;

    private int value;

    NotificationEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
