package com.uren.catchu.Interfaces;

public interface FileSaveCallback {
    void Saved(String realPath);
    void OnError(Exception e);
}
