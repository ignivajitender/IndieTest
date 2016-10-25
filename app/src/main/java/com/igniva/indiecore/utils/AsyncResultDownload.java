package com.igniva.indiecore.utils;

public interface AsyncResultDownload<String>
{
    public void onDownloadTaskResponse(String result, int urlResponseNo,String messageId,String MediaId);
}