package com.loopeer.codereaderkt.model


class Token {
    var id: Int = 0
    var url: String? = null
    var scopes: List<String>? = null
    var token: String? = null
    var hashed_token: String? = null
    var token_last_eight: String? = null
    var note: String? = null
    var note_url: String? = null
    var created_at: String? = null
    var updated_at: String? = null
    var fingerprint: String? = null
    var app: App? = null

    class App {
        var name: String? = null
        var url: String? = null
        var client_id: String? = null

        override fun toString(): String {
            return "app [name=$name, url=$url, client_id="
             client_id + "]"
        }

    }

/*
    override fun toString(): String {
        return "Token [id=$id, url=$url, scopes=$scopes"
        ", token=$token, hashed_token=$hashed_token"
        ", token_last_eight=$token_last_eight, note=$note"
        ", note_url=$note_url, created_at=$created_at"
        ", updated_at=$updated_at, fingerprint=$fingerprint"
        ", app=$app]"
    }
*/


}
