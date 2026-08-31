package com.github.Ramble21.commands.messageguessr;

public record Message(String content, String jumpUrl, long timestamp, long userId, long messageId, long channelId,
                      long serverId, long replyingToId) {
}
