# Spring Integration Demo

This is a sample application demoing two different ways of implementing spring integration for basic file moving.


## API Reference

- **MessageSource**
  - Base interface for any source of `Messages` that can be polled.
- **FileReadingMessageSource**
  - `MessageSource` that creates messages from a file system directory. To prevent messages for certain files,
    you may supply a `FileListFilter`. By default, when configuring with XML or the DSL, an `AcceptOnceFileListFilter`
    is used. It ensures files are picked up only once from the directory.
- **FileWritingMessageHandler**
  - A MessageHandler implementation that writes the Message payload to a file. If the payload is a File object,
    it will copy the File to the specified destination directory. If the payload is a byte array, a String or an
    InputStream it will be written directly. Otherwise, the payload type is unsupported, and an Exception will be thrown.
