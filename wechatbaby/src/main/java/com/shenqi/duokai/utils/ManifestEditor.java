package com.shenqi.duokai.utils;


import android.util.Log;

import pxb.android.axml.AxmlReader;
import pxb.android.axml.AxmlVisitor;
import pxb.android.axml.AxmlWriter;
import pxb.android.axml.NodeVisitor;

public class ManifestEditor {

    static boolean needRemoveConflict;
    static boolean needRemoveLib;
    //static String newPackageFullName;
    static String newPackgeSequence;

    static String newApplicationLabel;
    static boolean changed;

    public static byte[] ModifyAPKPackageName(byte[] manifestData, int number, String appLabel) {
        try {
            /*if (args.length != 2) {
                LOG("need parameters: androidManifestBinXml newPackageFullName");
                LOG("Exmaple: someDir/AndroidManifest.xml com.example.newapp");
                LOG("Exmaple: someDir/AndroidManifest.xml com.example.newapp!");
                LOG("Note:");
                LOG(" if newPackageFullName ends with ! then it will remove conflict settings:");
                LOG("   <original-package>,<provider>,android:protectionLevel,process,sharedUserId.");
                LOG("");
                LOG("This utility changes APK's package name (not java package name) and ");
                LOG("  prepend java package name to partial class name in AndroidManifest.xml:");
                LOG("  Application,Activity,Receiver,Service... ");
                LOG("  backupAgent,manageSpaceActivity,targetActivity... ");
                LOG("  meta value(only if start with dot)");
                System.exit(1);
            }*/


            needRemoveConflict = false;// args[1].contains("!");
            needRemoveLib = false;//args[1].contains("%");
            newPackgeSequence = String.format("%02d", number);
            newApplicationLabel = appLabel;

            AxmlReader ar = new AxmlReader(manifestData);
            AxmlWriter aw = new AxmlWriter();
            ar.accept(new AxmlVisitor(aw) {

                @Override
                public NodeVisitor child(String ns, String name) {
                    return new MyNodeVisitor(super.child(ns, name), name);
                }
            });

            if (changed) {
                return aw.toByteArray();
            } else {
                return manifestData;
                //System.exit(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(1);
            return null;
        }
    }

    static String NS = "http://schemas.android.com/apk/res/android";

    static class MyNodeVisitor extends NodeVisitor {
        static String level = "";
        static String oldPackageName;
        boolean didLogNodeName = false;
        String nodeName = "";

        MyNodeVisitor(NodeVisitor nv, String nodeName) {
            super(nv);
            this.nodeName = nodeName;
        }

        @Override
        public NodeVisitor child(String ns, String name) {
            /*
            if (needRemoveConflict && ("original-package".equals(name) || "provider".equals(name)) && ns == null) {
                LOG("x   " + level + "<" + name + "> will be removed");
                changed = true;
                return null;
            } else if (needRemoveLib && ("uses-library".equals(name)) && ns == null) {
                LOG("x   " + level + "<" + name + "> will be removed");
                changed = true;
                return null;
            }*/

            level += "    ";
            return new MyNodeVisitor(super.child(ns, name), name);
        }

        @Override
        public void attr(String ns, String name, int resourceId, int type, Object val) {
            String oldName = name;
            Object oldVal = val;
            /*
            if (ns == null && "package".equals(name) && "manifest".equals(nodeName) && type == NodeVisitor.TYPE_STRING && level.length() == 0) {
                oldPackageName = (String) val;
                if (!newPackageFullName.equals(val)) {
                    val = newPackageFullName;
                }
            } else if (type == NodeVisitor.TYPE_STRING && ("name".equals(name) || "backupAgent".equals(name) || "manageSpaceActivity".equals(name) || "targetActivity".equals(name)) && NS.equals(ns) && val != null && val instanceof String) {
                if (((String) val).startsWith(".")) {
                    val = oldPackageName + val;
                } else if (!((String) val).contains(".") && ((String) val).length() > 0) {
                    val = oldPackageName + "." + val;
                }

                int i;
                i = 3;
            } else if (type == NodeVisitor.TYPE_STRING && "value".equals(name) && NS.equals(ns) && val != null && val instanceof String) {
                if (((String) val).startsWith(".")) {
                    val = oldPackageName + val;
                }

                int i;
                i = 4;
            } else if (needRemoveConflict && ("protectionLevel".equals(name) || "process".equals(name) || "sharedUserId".equals(name)) && NS.equals(ns)) {
                name = null;
            } else if (needRemoveConflict && ("coreApp".equals(name)) && ns == null) {
                name = null;
            }*/

           if (ns == null && "package".equals(name) && "manifest".equals(nodeName) && type == NodeVisitor.TYPE_STRING && level.length() == 0) {
                oldPackageName = (String) val;
                val = "com.shenqi.pluginstub" + newPackgeSequence;

            }else if (type == NodeVisitor.TYPE_STRING && "name".equals(name) && "permission".equals(nodeName) && NS.equals(ns)) {
                if ("com.tencent.mm.permission.MM_MESSAGE_01".equals(val))
                    val = "com.tencent.mm.permission.MM_MESSAGE_" + newPackgeSequence;

            }else if (type == NodeVisitor.TYPE_STRING && "name".equals(name) && "uses-permission".equals(nodeName) && NS.equals(ns)) {
                if ("com.tencent.mm.permission.MM_MESSAGE_01".equals(val))
                    val = "com.tencent.mm.permission.MM_MESSAGE_" + newPackgeSequence;

            }else if (type == NodeVisitor.TYPE_STRING && "label".equals(name) && "application".equals(nodeName) && NS.equals(ns)) {
                val = newApplicationLabel;
            }else if (type == NodeVisitor.TYPE_STRING && "name".equals(name) && "action".equals(nodeName) && NS.equals(ns)) {
                if ("com.shenqi.pluginstub01.MyProtect".equals(val))
                    val = "com.shenqi.pluginstub" + newPackgeSequence + ".MyProtect";
            }else if (type == NodeVisitor.TYPE_STRING && "authorities".equals(name) && "provider".equals(nodeName) && NS.equals(ns)) {
                String oldAuth = (String)val;
                String oldPrefix = "com.morgoo.droidplugin_auth01";
                int nLen = oldPrefix.length();
                String oldSuffix = oldAuth.substring(nLen);

                if (oldAuth.substring(0, nLen).equals(oldPrefix)) {
                    String newAuth = "com.morgoo.droidplugin_auth" + newPackgeSequence + oldSuffix;
                    val = newAuth;
                }
            }

            if (name != oldName || val != oldVal) {
                changed = true;
                if (!didLogNodeName) {
                    didLogNodeName = true;
                    LOG(level + "<" + nodeName + ">");
                }
                if (name == null) {
                    LOG("x   " + level + oldName + "=" + oldVal + " will be removed");
                    return;
                } else {
                    LOG(level + "    " + oldName + "=" + oldVal);
                    LOG("=>  " + level + name + "=" + val);
                }
            }

            super.attr(ns, name, resourceId, type, val);
        }

        @Override
        public void end() {
            level = level.length() > 4 ? level.substring(4) : "";
            super.end();
        }
    }

    static void LOG(String s) {
        //System.err.println(s);
        Log.d("TESTSIGN", s);
    }
}
