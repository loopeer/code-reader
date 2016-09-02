package com.loopeer.codereader.model;

import java.util.List;


/**
 *
 * @author Quinn
 *
 */
public class Token {
    private int id;
    private String url;
    private List<String> scopes;
    private String token;
    private String hashed_token;
    private String token_last_eight;
    private String note;
    private String note_url;
    private String created_at;
    private String updated_at;
    private String fingerprint;
    private App app;
    private static class App{
        private String name;
        private String url;
        private String client_id;

        public App(){

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        @Override
        public String toString() {
            return "app [name=" + name + ", url=" + url + ", client_id="
                    + client_id + "]";
        }

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public List<String> getScopes() {
        return scopes;
    }
    public void setScopes(List<String> scropes) {
        this.scopes = scropes;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getHashed_token() {
        return hashed_token;
    }
    public void setHashed_token(String hashed_token) {
        this.hashed_token = hashed_token;
    }
    public String getToken_last_eight() {
        return token_last_eight;
    }
    public void setToken_last_eight(String token_last_eight) {
        this.token_last_eight = token_last_eight;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getNote_url() {
        return note_url;
    }
    public void setNote_url(String note_url) {
        this.note_url = note_url;
    }
    public String getCreated_at() {
        return created_at;
    }
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    public String getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    public String getFingerprint() {
        return fingerprint;
    }
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
    public App getApp() {
        return app;
    }
    public void setApp(App _app) {
        this.app = _app;
    }

    @Override
    public String toString() {
        return "Token [id=" + id + ", url=" + url + ", scopes=" + scopes
                + ", token=" + token + ", hashed_token=" + hashed_token
                + ", token_last_eight=" + token_last_eight + ", note=" + note
                + ", note_url=" + note_url + ", created_at=" + created_at
                + ", updated_at=" + updated_at + ", fingerprint=" + fingerprint
                + ", app=" + app + "]";
    }



}
