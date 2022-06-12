package com.colwanymab.timelynews;

public final class DBContract {

    public DBContract() {
    }


    static abstract class Recyclers {
        static final String TABLE_NAME = "articlesz";
        static final String ID = "_id";
        static final String UID = "uidz";
        static final String NAME = "art_key";
        static final String CITY = "headline_id";
        static final String NUMBER = "article_story";
        static final String TYPE = "image_link_one";
        static final String LINK = "image_link_two";
        static final String EMAIL = "art_like";
        static final String ADDRESS = "like_number";
        static final String LAT = "comment_number";
        static final String LONG = "art_timestamp";
    }

    static abstract class ConfirmationMessage {
        static final String TABLE_NAME = "presidential_statements";
        static final String ID = "_id";
        static final String UID = "uidz";
        static final String KEY = "art_key";
        static final String MESSAGE = "headline_id";
        static final String PHONE = "article_story";
        static final String WHATS_APP = "image_link_one";
        static final String IMAGE_LINK_2 = "image_link_two";
        static final String LIKE = "art_like";
        static final String LIKE_NUMBER = "like_number";
        static final String COMMENT_NUMBER = "comment_number";
        static final String TIMESTAMP = "art_timestamp";
    }

    static abstract class News {
        static final String TABLE_NAME = "comments_app";
        static final String ID = "_id";
        static final String KEY = "uidz";
        static final String WRITER_UID = "comment_key";
        static final String EDITOR_UID = "comment_post_key";
        static final String HEADLINE = "username";
        static final String STORY = "the_comment";
        static final String READ_TIME = "comment_timestamp";
        static final String READ = "commenter_province";
        static final String TIMESTAMP = "date_wasman";
        static final String LINK = "link_wasman";
        static final String TAG = "tag_wasman";
        static final String FREE = "free_news";
    }

    static abstract class Messages {
        static final String TABLE_NAME = "app_messages";
        static final String ID = "_id";
        static final String UID = "uidz";
        static final String CHAT_ROOM = "sender_uid";
        static final String LINK = "user_name";
        static final String KEY = "comment_key";
        static final String MESSAGE = "dm_message";
        static final String ISTEXT = "is_texting";
        static final String TIMESTAMP = "message_timestamp";
    }

    static abstract class WasteCollection {
        static final String TABLE_NAME = "party_structures";
        static final String ID = "_id";
        static final String PHONE = "editor_uid";
        static final String KEY = "structure_key";
        static final String PRICE = "member_name";
        static final String LAT = "member_province";
        static final String LONG = "member_position";
        static final String LINK = "member_link";
    }

    static abstract class Events {
        static final String TABLE_NAME = "party_eventsz";
        static final String ID = "_id";
        static final String KEY = "party_events_key";
        static final String NAME = "event_name";
        static final String VENUE = "event_venues";
        static final String DATE = "member_province";
        static final String LINK = "member_link";
        static final String START_DATE = "start_date_events";
        static final String UID = "end_date_events";
        static final String LAT = "event_start_timestamp";
        static final String LONG = "event_end_timestamp";
    }



}
