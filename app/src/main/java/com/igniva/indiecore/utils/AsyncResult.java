package com.igniva.indiecore.utils;

public interface AsyncResult<String>
{
    public void onTaskResponse(String result, int urlResponseNo);
}