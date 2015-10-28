package jp.ac.nagoya_u.is.ss.kishii.usui.system.game;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * C++で作成されたプレイヤーのクラスを呼び出すためのクラスローダーです。
 * 各プレイヤーのクラスローダーを分け，異なるライブラリによる動作を実現するためだけに用いられます。
 * その他の目的で正しく動作するかは不明です。
 *
 */
class CppClassLoader extends URLClassLoader {

    private Map<String, Class<?>> classes;

    public CppClassLoader(URL[] urls) {
        super(urls, CppClassLoader.class.getClassLoader());
        classes = new HashMap<String, Class<?>>();
    }

    public String toString() {
        return CppClassLoader.class.getName();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
    	if (classes.containsKey(name)) {
    		return classes.get(name);
    	} else {
    		Class<?> clazz = super.findClass(name);
    		classes.put(name, clazz);

    		return clazz;
    	}
    }
}