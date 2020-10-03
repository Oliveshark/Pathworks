package com.oliveshark.pathworks.framework.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserCallback;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.util.function.Consumer;

public class PathMapFileChooser {

    private NativeFileChooser nativeFileChooser;

    private NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();

    public PathMapFileChooser(NativeFileChooser nativeFileChooser) {
        this.nativeFileChooser = nativeFileChooser;
        conf.directory = Gdx.files.absolute(System.getProperty("user.dir") + "/storage");
        conf.nameFilter = (dir, name) -> name.endsWith(".pathMap");
        conf.title = "Choose pathMap location";
    }

    public void chooseFile(Consumer<FileHandle> fileHandleConsumer) {
        chooseFile(fileHandleConsumer, null, null);
    }

    public void chooseFile(Consumer<FileHandle> fileHandleConsumer, Runnable onCancel) {
        chooseFile(fileHandleConsumer, onCancel, null);
    }

    public void chooseFile(Consumer<FileHandle> fileHandleConsumer, Runnable onCancel, Consumer<Exception> onError) {
        nativeFileChooser.chooseFile(conf, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                if (fileHandleConsumer != null) {
                    fileHandleConsumer.accept(file);
                }
            }

            @Override
            public void onCancellation() {
                if (onCancel != null) {
                    onCancel.run();
                }
            }

            @Override
            public void onError(Exception exception) {
                if (onError != null) {
                    onError.accept(exception);
                }
            }
        });
    }
}
