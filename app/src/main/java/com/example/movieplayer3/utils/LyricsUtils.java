package com.example.movieplayer3.utils;

import android.util.Log;

import com.example.movieplayer3.domain.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by chenyuelun on 2017/5/26.
 */

public class LyricsUtils {
    private ArrayList<Lyric> lyrics = new ArrayList<>();

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }


    public void readLyric(File lrc) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(lrc), getCharset(lrc)));
            String line;
            while ((line = reader.readLine()) != null) {
                parseLysicOfLine(line);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(lyrics, new Comparator<Lyric>() {
            @Override
            public int compare(Lyric o1, Lyric o2) {
                if (o1.getTimePoint() < o2.getTimePoint()) {
                    return -1;
                } else if (o1.getTimePoint() > o2.getTimePoint()) {
                    return 1;
                }
                return 0;
            }
        });

        for (int i = 0; i < lyrics.size(); i++) {
            Log.e("TAG",lyrics.get(i).toString());
            Lyric lyric1 = lyrics.get(i);
            if(i+1 < lyrics.size()) {
                Lyric lyric2 = lyrics.get(i + 1);
                lyric1.setDuration(lyric2.getTimePoint() - lyric1.getTimePoint());
                Log.e("TAG",lyric1.toString());
            }
        }


    }

    private String getCharset(File lrc) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(lrc));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    private void parseLysicOfLine(String line) {
        int pos1 = line.indexOf("[");
        int pos2 = line.indexOf("]");

        if (pos1 == 0 && pos2 != -1) {
            long[] timeLongs = new long[getCountTags(line)];
            String timeStr = line.substring(pos1 + 1, pos2);
            timeLongs[0] = str2Long(timeStr);
            if (timeLongs[0] == -1) {
                return;
            }
            int i = 1;
            String content = line;
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2+1);
                pos1 = content.indexOf("[");
                pos2 = content.indexOf("]");
                if (pos1 == 0 && pos2 != -1) {
                    timeLongs = new long[getCountTags(line)];
                    timeStr = content.substring(pos1 + 1, pos2);
                    timeLongs[i] = str2Long(timeStr);
                    if (timeLongs[i] == -1) {
                        return;
                    }
                    i++;
                }

            }

            for (int j = 0; j < timeLongs.length; j++) {
                if (timeLongs[j] != 0) {
                    Lyric lyric = new Lyric();
                    lyric.setContent(content);
                    lyric.setTimePoint(timeLongs[j]);

                    lyrics.add(lyric);
                }

            }

        }


    }

    private long str2Long(String timeStr) {
        long result = -1;
        try {
            String[] ss1 = timeStr.split("\\:");
            String[] ss2 = ss1[1].split("\\.");
            long m = Long.valueOf(ss1[0]);
            long s = Long.valueOf(ss2[0]);
            long ms = Long.valueOf(ss2[1]);
            result = m * 60 * 1000 + s * 1000 + ms * 10;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return result;
    }

    private int getCountTags(String line) {
        String[] s1 = line.split("\\[");
        String[] s2 = line.split("\\]");

        if (s1.length == 0 && s2.length == 0) {
            return 1;
        } else {
            return s1.length;
        }
    }
}
