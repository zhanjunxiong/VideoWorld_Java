package com.lxw.videoworld.spider;

import com.lxw.videoworld.config.Constants;
import com.lxw.videoworld.domain.SourceDetail;
import com.lxw.videoworld.utils.HTMLUtil;
import com.lxw.videoworld.utils.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.util.TextUtils;
import us.codecraft.webmagic.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxw9047 on 2017/4/28.
 */
public class YgdySourceDetailProcessor extends BaseYgdyProcessor {
    @Override
    public void process(Page page) {
        super.process(page);
        SourceDetail sourceDetail = new SourceDetail();
        String url = page.getUrl().toString();
        if (!TextUtils.isEmpty(url)) {
            String[] params = url.split("/");
            if (params.length == 8) {
                if (!TextUtils.isEmpty(params[7]) && params[7].length() > 5) {
                    sourceDetail.setId(params[7].substring(0, params[7].length() - 5));
                }
                sourceDetail.setCategory(params[4]);
                sourceDetail.setType(params[5]);
                if (!TextUtils.isEmpty(params[6]) && params[6].length() == 8) {
                    sourceDetail.setDate(Integer.valueOf(params[6]));
                }
            } else {

            }
        }
        String title = page.getHtml().css("div.title_all").css("font").regex(">(.*?)</font>").get();
        List<String> imgUrl = page.getHtml().css("div.co_content8").css("img", "src").all();
        String content = "";
        String content0 = page.getHtml().css("div#Zoom").toString();
        List<String> imgs = page.getHtml().css("div#Zoom").css("img").all();
        try {
            if (!TextUtils.isEmpty(content0) && imgs != null) {
                if (imgs.size() >= 2) {
                    int start = content0.indexOf(imgs.get(0)) + imgs.get(0).length();
                    int end = content0.indexOf(imgs.get(1));
                    content = content0.substring(start, end);
                    content = StringEscapeUtils.unescapeHtml(content).replaceAll("<br/>", "\n").replaceAll("<br>", "\n")
                            .replaceAll("<br />", "\n").replaceAll("<p>", "").replaceAll("</p>", "\n");
                    content = HTMLUtil.trimHtml2Txt(content, null);
                    if (!TextUtils.isEmpty(content.trim()) && content.trim().contains("【简 &nbsp; &nbsp;　介】：")) {
                        int start2 = content0.indexOf(imgs.get(1)) + imgs.get(1).length();
                        int end2 = content0.indexOf(imgs.get(2));
                        String temp = content0.substring(start2, end2);
                        if (!TextUtils.isEmpty(temp)) {
                            content = content + temp;
                        }
                    }
                    if (TextUtils.isEmpty(content.trim()) && imgs.size() >= 3) {
                        int start1 = content0.indexOf(imgs.get(1)) + imgs.get(1).length();
                        int end1 = content0.indexOf(imgs.get(2));
                        content = content0.substring(start1, end1);
                        content = StringEscapeUtils.unescapeHtml(content).replaceAll("<br/>", "\n").replaceAll("<br>", "\n")
                                .replaceAll("<br />", "\n").replaceAll("<p>", "").replaceAll("</p>", "\n");
                        content = HTMLUtil.trimHtml2Txt(content, null);
                        if (TextUtils.isEmpty(content.trim()) && imgs.size() >= 4) {
                            int start2 = content0.indexOf(imgs.get(2)) + imgs.get(2).length();
                            int end2 = content0.indexOf(imgs.get(3));
                            content = content0.substring(start2, end2);
                        }
                    }
                } else if (imgs.size() == 1) {
                    int start = content0.indexOf(imgs.get(0)) + imgs.get(0).length();
                    int end = content0.indexOf("下载地址");
                    content = content0.substring(start, end);
                } else if (imgs.size() == 0) {
                    String div = "<div id=\"Zoom\">";
                    int start = content0.indexOf(div) + div.length();
                    int end = content0.indexOf("下载地址");
                    content = content0.substring(start, end);
                }
            }
            content = StringEscapeUtils.unescapeHtml(content).replaceAll("<br/>", "\n").replaceAll("<br>", "\n")
                    .replaceAll("<br />", "\n").replaceAll("<p>", "").replaceAll("</p>", "\n");
            content = HTMLUtil.trimHtml2Txt(content, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> links = new ArrayList<>();
        List<String> links0 = page.getHtml().css("div.co_content8").css("table").links().all();
        if (links0 != null && links0.size() > 0) {
            for (int i = 0; i < links0.size(); i++) {
                if (!links0.get(i).contains("html")) {
                    links.add(links0.get(i));
                }
            }
        }

        sourceDetail.setUrl(url);
        if (!TextUtils.isEmpty(title)) {
            sourceDetail.setTitle(StringUtil.disposeField(title));
        }
        if (imgUrl != null && imgUrl.size() > 0) {
            sourceDetail.setImages(imgUrl.toString());
        }
        if (links != null && links.size() > 0) {
            sourceDetail.setLinks(links.toString());
        }
        if (!TextUtils.isEmpty(content)) {
            String temp = StringUtil.disposeField(content);
            if (!TextUtils.isEmpty(temp) && temp.substring(temp.length() - 1, temp.length()).equals("【")) {
                temp = temp.substring(0, temp.length() - 1);
            }
            sourceDetail.setContent(temp.trim());
        }
        sourceDetail.setStatus(Constants.STATUS_2);

        // 获取视频详情各种属性
        if (!TextUtils.isEmpty(sourceDetail.getCategory()) && !sourceDetail.getCategory().equals(Constants.CATEGORY_2) && !sourceDetail.getCategory().equals(Constants.CATEGORY_3)) {
            if (sourceDetail.getCategory().equals(Constants.CATEGORY_5)) {
                String name = page.getHtml().css("div#Zoom").regex("中文名称:(.*?)<").toString();
                if (!TextUtils.isEmpty(name)) {
                    sourceDetail.setName(StringUtil.disposeField(name));
                }
                String style = page.getHtml().css("div#Zoom").regex("游戏类型:(.*?)<").toString();
                if (!TextUtils.isEmpty(style)) {
                    sourceDetail.setStyle(StringUtil.disposeField(style));
                }
            } else {
                String name = page.getHtml().css("div#Zoom").regex("◎片　　名(.*?)<").toString();
                if (!TextUtils.isEmpty(name)) {
                    name = name.replaceAll("</font>", "");
                    sourceDetail.setName(StringUtil.disposeField(name));
                } else {
                    String name1 = page.getHtml().css("div#Zoom").regex("【原 &nbsp; &nbsp;　名】：(.*?)<").toString();
                    if (!TextUtils.isEmpty(name1)) {
                        name1 = name1.replaceAll("</font>", "");
                        sourceDetail.setName(StringUtil.disposeField(name1));
                    } else {
                        String name2 = page.getHtml().css("div#Zoom").regex("[剧 名]:(.*?)<").toString();
                        if (!TextUtils.isEmpty(name2)) {
                            name2 = name2.replaceAll("</font>", "");
                            sourceDetail.setName(StringUtil.disposeField(name2));
                        } else {
                            String name3 = page.getHtml().css("div#Zoom").regex("【片 &nbsp; &nbsp;　名】：(.*?)<").toString();
                            if (!TextUtils.isEmpty(name3)) {
                                name3 = name3.replaceAll("</font>", "");
                                sourceDetail.setName(StringUtil.disposeField(name3));
                            }
                        }
                    }
                }
                String translate_name = page.getHtml().css("div#Zoom").regex("◎译　　名(.*?)<").toString();
                if (!TextUtils.isEmpty(translate_name)) {
                    translate_name = translate_name.replaceAll("</font>", "");
                    sourceDetail.setTranslateName(StringUtil.disposeField(translate_name));
                } else {
                    String translate_name1 = page.getHtml().css("div#Zoom").regex("【译 &nbsp; &nbsp;　名】：(.*?)<").toString();
                    if (!TextUtils.isEmpty(translate_name1)) {
                        translate_name1 = translate_name1.replaceAll("</font>", "");
                        sourceDetail.setTranslateName(StringUtil.disposeField(translate_name1));
                    }
                }
                try {
                    String year = page.getHtml().css("div#Zoom").regex("◎年　　代(.*?)<").toString();
                    if (!TextUtils.isEmpty(year)) {
                        year = year.replaceAll("</font>", "");
                        sourceDetail.setYear(Integer.valueOf(StringUtil.disposeField(year)));
                    } else {
                        String year1 = page.getHtml().css("div#Zoom").regex("【年 &nbsp; &nbsp;　代】：(.*?)<").toString();
                        if (!TextUtils.isEmpty(year1)) {
                            year1 = year1.replaceAll("</font>", "");
                            sourceDetail.setYear(Integer.valueOf(StringUtil.disposeField(year1)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String area = page.getHtml().css("div#Zoom").regex("◎产　　地(.*?)<").toString();
                if (!TextUtils.isEmpty(area)) {
                    area = area.replaceAll("</font>", "");
                    sourceDetail.setArea(StringUtil.disposeField(area));
                } else {
                    String area1 = page.getHtml().css("div#Zoom").regex("◎国　　家(.*?)<").toString();
                    if (!TextUtils.isEmpty(area1)) {
                        area1 = area1.replaceAll("</font>", "");
                        sourceDetail.setArea(StringUtil.disposeField(area1));
                    } else {
                        String area2 = page.getHtml().css("div#Zoom").regex("【国 &nbsp; &nbsp;　家】：(.*?)<").toString();
                        if (!TextUtils.isEmpty(area2)) {
                            area2 = area2.replaceAll("</font>", "");
                            sourceDetail.setArea(StringUtil.disposeField(area2));
                        }
                    }
                }
                String style = page.getHtml().css("div#Zoom").regex("◎类　　别(.*?)<").toString();
                if (!TextUtils.isEmpty(style)) {
                    style = style.replaceAll("</font>", "");
                    sourceDetail.setStyle(StringUtil.disposeField(style));
                } else {
                    String style1 = page.getHtml().css("div#Zoom").regex("【类 &nbsp; &nbsp;　别】：(.*?)<").toString();
                    if (!TextUtils.isEmpty(style1)) {
                        style1 = style1.replaceAll("</font>", "");
                        sourceDetail.setStyle(StringUtil.disposeField(style1));
                    } else {
                        String style2 = page.getHtml().css("div#Zoom").regex("[类 型]:(.*?)<").toString();
                        if (!TextUtils.isEmpty(style2)) {
                            style2 = style2.replaceAll("</font>", "");
                            sourceDetail.setStyle(StringUtil.disposeField(style2));
                        }
                    }
                    String language = page.getHtml().css("div#Zoom").regex("◎语　　言(.*?)<").toString();
                    if (!TextUtils.isEmpty(language)) {
                        language = language.replaceAll("</font>", "");
                        sourceDetail.setLanguage(StringUtil.disposeField(language));
                    } else {
                        String language1 = page.getHtml().css("div#Zoom").regex("【语 &nbsp; &nbsp;　言】：(.*?)<").toString();
                        if (!TextUtils.isEmpty(language1)) {
                            language1 = language1.replaceAll("</font>", "");
                            sourceDetail.setLanguage(StringUtil.disposeField(language1));
                        }
                    }
                    String subtitles = page.getHtml().css("div#Zoom").regex("◎字　　幕(.*?)<").toString();
                    if (!TextUtils.isEmpty(subtitles)) {
                        subtitles = subtitles.replaceAll("</font>", "");
                        sourceDetail.setSubtitles(StringUtil.disposeField(subtitles));
                    } else {
                        String subtitles1 = page.getHtml().css("div#Zoom").regex("【字 &nbsp; &nbsp;　幕】：(.*?)<").toString();
                        if (!TextUtils.isEmpty(subtitles1)) {
                            subtitles1 = subtitles1.replaceAll("</font>", "");
                            sourceDetail.setSubtitles(StringUtil.disposeField(subtitles1));
                        }
                    }
                    String release_date = page.getHtml().css("div#Zoom").regex("◎上映日期(.*?)<").toString();
                    if (!TextUtils.isEmpty(release_date)) {
                        release_date = release_date.replaceAll("</font>", "");
                        sourceDetail.setReleaseDate(StringUtil.disposeField(release_date));
                    } else {
                        String release_date1 = page.getHtml().css("div#Zoom").regex("【首 &nbsp; &nbsp;　播】：(.*?)<").toString();
                        if (!TextUtils.isEmpty(release_date1)) {
                            release_date1 = release_date1.replaceAll("</font>", "");
                            sourceDetail.setReleaseDate(StringUtil.disposeField(release_date1));
                        } else {
                            String release_date2 = page.getHtml().css("div#Zoom").regex("[首 播]:(.*?)<").toString();
                            if (!TextUtils.isEmpty(release_date2)) {
                                release_date2 = release_date2.replaceAll("</font>", "");
                                sourceDetail.setReleaseDate(StringUtil.disposeField(release_date2));
                            }
                        }
                        try {
                            String imdb_score0 = page.getHtml().css("div#Zoom").regex("◎IMDb评分(.*?)<").toString();
                            if (!TextUtils.isEmpty(imdb_score0)) {
                                imdb_score0 = imdb_score0.replaceAll("</font>", "");
                                String[] imdb_score = imdb_score0.trim().split("/");
                                if (imdb_score.length > 0 && !TextUtils.isEmpty(imdb_score[0])) {
                                    sourceDetail.setImdbScore(Float.valueOf(StringUtil.disposeField(imdb_score[0])));
                                }
                                if (imdb_score.length > 1 && !TextUtils.isEmpty(imdb_score[1])) {
                                    sourceDetail.setImdbUrl(StringUtil.disposeField(imdb_score[1]));
                                }
                            } else {
                                String imdb_score1 = page.getHtml().css("div#Zoom").regex("【IMDB评分】：(.*?)<").toString();
                                if (!TextUtils.isEmpty(imdb_score1)) {
                                    imdb_score1 = imdb_score1.replaceAll("</font>", "");
                                    String[] imdb_score = imdb_score1.trim().split("/");
                                    if (imdb_score.length > 0 && !TextUtils.isEmpty(imdb_score[0])) {
                                        sourceDetail.setImdbScore(Float.valueOf(StringUtil.disposeField(imdb_score[0])));
                                    }
                                    if (imdb_score.length > 1 && !TextUtils.isEmpty(imdb_score[1])) {
                                        sourceDetail.setImdbUrl(StringUtil.disposeField(imdb_score[1]));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            String douban_score0 = page.getHtml().css("div#Zoom").regex("◎豆瓣评分(.*?)<").toString();
                            if (!TextUtils.isEmpty(douban_score0)) {
                                douban_score0 = douban_score0.replaceAll("</font>", "");
                                String[] douban_score = douban_score0.trim().split("/");
                                if (douban_score.length > 0 && !TextUtils.isEmpty(douban_score[0])) {
                                    sourceDetail.setDoubanScore(Float.valueOf(StringUtil.disposeField(douban_score[0])));
                                }
                                if (douban_score.length > 1 && !TextUtils.isEmpty(douban_score[1])) {
                                    sourceDetail.setDoubanIntro(StringUtil.disposeField(douban_score[1]));
                                }
                            } else {
                                String douban_score1 = page.getHtml().css("div#Zoom").regex("【豆瓣评分】：(.*?)<").toString();
                                if (!TextUtils.isEmpty(douban_score1)) {
                                    douban_score1 = douban_score1.replaceAll("</font>", "");
                                    String[] douban_score = douban_score1.trim().split("/");
                                    if (douban_score.length > 0 && !TextUtils.isEmpty(douban_score[0])) {
                                        sourceDetail.setDoubanScore(Float.valueOf(StringUtil.disposeField(douban_score[0])));
                                    }
                                    if (douban_score.length > 1 && !TextUtils.isEmpty(douban_score[1])) {
                                        sourceDetail.setDoubanIntro(StringUtil.disposeField(douban_score[1]));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String file_format = page.getHtml().css("div#Zoom").regex("◎文件格式(.*?)<").toString();
                        if (!TextUtils.isEmpty(file_format)) {
                            file_format = file_format.replaceAll("</font>", "");
                            sourceDetail.setFileFormat(StringUtil.disposeField(file_format));
                        }
                        String file_size = page.getHtml().css("div#Zoom").regex("◎视频尺寸(.*?)<").toString();
                        if (!TextUtils.isEmpty(file_size)) {
                            file_size = file_size.replaceAll("</font>", "");
                            sourceDetail.setFileSize(StringUtil.disposeField(file_size));
                        }
                        String file_amounts = page.getHtml().css("div#Zoom").regex("◎文件大小(.*?)<").toString();
                        if (!TextUtils.isEmpty(file_amounts)) {
                            file_amounts = file_amounts.replaceAll("</font>", "");
                            sourceDetail.setFileAmounts(StringUtil.disposeField(file_amounts));
                        }
                        String file_length = page.getHtml().css("div#Zoom").regex("◎片　　长(.*?)<").toString();
                        if (!TextUtils.isEmpty(file_length)) {
                            file_length = file_length.replaceAll("</font>", "");
                            sourceDetail.setFileLength(StringUtil.disposeField(file_length));
                        } else {
                            String file_length1 = page.getHtml().css("div#Zoom").regex("【片 &nbsp; &nbsp;　长】：(.*?)<").toString();
                            if (!TextUtils.isEmpty(file_length1)) {
                                file_length1 = file_length1.replaceAll("</font>", "");
                                sourceDetail.setFileLength(StringUtil.disposeField(file_length1));
                            }
                        }
                        String episodes = page.getHtml().css("div#Zoom").regex("◎集　　数(.*?)<").toString();
                        if (!TextUtils.isEmpty(episodes)) {
                            episodes = episodes.replaceAll("</font>", "");
                            sourceDetail.setEpisodes(StringUtil.disposeField(episodes));
                        } else {
                            String episodes1 = page.getHtml().css("div#Zoom").regex("【集 &nbsp; &nbsp;　数】：(.*?)<").toString();
                            if (!TextUtils.isEmpty(episodes1)) {
                                episodes1 = episodes1.replaceAll("</font>", "");
                                sourceDetail.setEpisodes(StringUtil.disposeField(episodes1));
                            } else {
                                String episodes2 = page.getHtml().css("div#Zoom").regex("[集 数]:(.*?)<").toString();
                                if (!TextUtils.isEmpty(episodes2)) {
                                    episodes2 = episodes2.replaceAll("</font>", "");
                                    sourceDetail.setEpisodes(StringUtil.disposeField(episodes2));
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(sourceDetail.getContent())) {
                            String str = sourceDetail.getContent();
                            if (str.contains("◎编　　剧")) {
                                String[] author0 = str.split("◎编　　剧");
                                if (author0.length > 1 && !TextUtils.isEmpty(author0[1])) {
                                    String[] temp = author0[1].split("◎");
                                    sourceDetail.setAuthor(StringUtil.disposeField(temp[0]));
                                } else if (author0.length == 1 && !TextUtils.isEmpty(author0[0])) {
                                    String[] temp = author0[0].split("◎");
                                    sourceDetail.setAuthor(StringUtil.disposeField(temp[0]));
                                }
                            } else {
                                if (str.contains("【编 &nbsp; &nbsp;　剧】：")) {
                                    String[] author0 = str.split("【编 &nbsp; &nbsp;　剧】：");
                                    if (author0.length > 1 && !TextUtils.isEmpty(author0[1])) {
                                        String[] temp = author0[1].split("【");
                                        sourceDetail.setAuthor(StringUtil.disposeField(temp[0]));
                                    } else if (author0.length == 1 && !TextUtils.isEmpty(author0[0])) {
                                        String[] temp = author0[0].split("【");
                                        sourceDetail.setAuthor(StringUtil.disposeField(temp[0]));
                                    }
                                } else {
                                    if (str.contains("[编 剧]:")) {
                                        String[] author0 = str.split("[编 剧]:");
                                        if (author0.length > 1 && !TextUtils.isEmpty(author0[1])) {
                                            String[] temp = author0[1].split("[");
                                            sourceDetail.setAuthor(StringUtil.disposeField(temp[0]));
                                        } else if (author0.length == 1 && !TextUtils.isEmpty(author0[0])) {
                                            String[] temp = author0[0].split("[");
                                            sourceDetail.setAuthor(StringUtil.disposeField(temp[0]));
                                        }
                                    }
                                }
                            }
                            if (str.contains("◎导　　演")) {
                                String[] director0 = str.split("◎导　　演");
                                if (director0.length > 1 && !TextUtils.isEmpty(director0[1])) {
                                    String[] temp = director0[1].split("◎");
                                    sourceDetail.setDirector(StringUtil.disposeField(temp[0]));
                                } else if (director0.length == 1 && !TextUtils.isEmpty(director0[0])) {
                                    String[] temp = director0[0].split("◎");
                                    sourceDetail.setDirector(StringUtil.disposeField(temp[0]));
                                }
                            } else {
                                if (str.contains("【导 &nbsp; &nbsp;　演】：")) {
                                    String[] director0 = str.split("【导 &nbsp; &nbsp;　演】：");
                                    if (director0.length > 1 && !TextUtils.isEmpty(director0[1])) {
                                        String[] temp = director0[1].split("【");
                                        sourceDetail.setDirector(StringUtil.disposeField(temp[0]));
                                    } else if (director0.length == 1 && !TextUtils.isEmpty(director0[0])) {
                                        String[] temp = director0[0].split("【");
                                        sourceDetail.setDirector(StringUtil.disposeField(temp[0]));
                                    }
                                } else {
                                    if (str.contains("[导 演]:")) {
                                        String[] director0 = str.split("[导 演]:");
                                        if (director0.length > 1 && !TextUtils.isEmpty(director0[1])) {
                                            String[] temp = director0[1].split("[");
                                            sourceDetail.setDirector(StringUtil.disposeField(temp[0]));
                                        } else if (director0.length == 1 && !TextUtils.isEmpty(director0[0])) {
                                            String[] temp = director0[0].split("[");
                                            sourceDetail.setDirector(StringUtil.disposeField(temp[0]));
                                        }
                                    }
                                }
                            }
                            if (str.contains("◎主　　演")) {
                                String[] performer0 = str.split("◎主　　演");
                                if (performer0.length > 1 && !TextUtils.isEmpty(performer0[1])) {
                                    String[] temp = performer0[1].split("◎");
                                    sourceDetail.setPerformer(StringUtil.disposeField(temp[0]));
                                } else if (performer0.length == 1 && !TextUtils.isEmpty(performer0[0])) {
                                    String[] temp = performer0[0].split("◎");
                                    sourceDetail.setPerformer(StringUtil.disposeField(temp[0]));
                                }
                            } else {
                                if (str.contains("【演 &nbsp; &nbsp;　员】：")) {
                                    String[] performer0 = str.split("【演 &nbsp; &nbsp;　员】：");
                                    if (performer0.length > 1 && !TextUtils.isEmpty(performer0[1])) {
                                        String[] temp = performer0[1].split("【");
                                        sourceDetail.setPerformer(StringUtil.disposeField(temp[0]));
                                    } else if (performer0.length == 1 && !TextUtils.isEmpty(performer0[0])) {
                                        String[] temp = performer0[0].split("【");
                                        sourceDetail.setPerformer(StringUtil.disposeField(temp[0]));
                                    }
                                } else {
                                    if (str.contains("[演 员]:")) {
                                        String[] performer0 = str.split("[演 员]:");
                                        if (performer0.length > 1 && !TextUtils.isEmpty(performer0[1])) {
                                            String[] temp = performer0[1].split("[");
                                            sourceDetail.setPerformer(StringUtil.disposeField(temp[0]));
                                        } else if (performer0.length == 1 && !TextUtils.isEmpty(performer0[0])) {
                                            String[] temp = performer0[0].split("[");
                                            sourceDetail.setPerformer(StringUtil.disposeField(temp[0]));
                                        }
                                    }
                                }
                            }
                            if (str.contains("◎简　　介")) {
                                String[] intro0 = str.split("◎简　　介");
                                if (intro0.length > 1 && !TextUtils.isEmpty(intro0[1])) {
                                    String[] temp = intro0[1].split("◎");
                                    sourceDetail.setIntro(StringUtil.disposeField(temp[0]));
                                } else if (intro0.length == 1 && !TextUtils.isEmpty(intro0[0])) {
                                    String[] temp = intro0[0].split("◎");
                                    sourceDetail.setIntro(StringUtil.disposeField(temp[0]));
                                }
                            } else {
                                if (str.contains("【简 &nbsp; &nbsp;　介】：")) {
                                    String[] intro0 = str.split("【简 &nbsp; &nbsp;　介】：");
                                    if (intro0.length > 1 && !TextUtils.isEmpty(intro0[1])) {
                                        String[] temp = intro0[1].split("【");
                                        sourceDetail.setIntro(StringUtil.disposeField(temp[0]));
                                    } else if (intro0.length == 1 && !TextUtils.isEmpty(intro0[0])) {
                                        String[] temp = intro0[0].split("【");
                                        sourceDetail.setIntro(StringUtil.disposeField(temp[0]));
                                    }
                                } else {
                                    if (str.contains("[简 介]:")) {
                                        String[] intro0 = str.split("[简 介]:");
                                        if (intro0.length > 1 && !TextUtils.isEmpty(intro0[1])) {
                                            String[] temp = intro0[1].split("[");
                                            sourceDetail.setIntro(StringUtil.disposeField(temp[0]));
                                        } else if (intro0.length == 1 && !TextUtils.isEmpty(intro0[0])) {
                                            String[] temp = intro0[0].split("[");
                                            sourceDetail.setIntro(StringUtil.disposeField(temp[0]));
                                        }
                                    }
                                }
                            }
                            if (str.contains("◎获奖情况")) {
                                String[] awards0 = str.split("◎获奖情况");
                                if (awards0.length > 1 && !TextUtils.isEmpty(awards0[1])) {
                                    String[] temp = awards0[1].split("◎");
                                    sourceDetail.setAwards(StringUtil.disposeField(temp[0]));
                                } else if (awards0.length == 1 && !TextUtils.isEmpty(awards0[0])) {
                                    String[] temp = awards0[0].split("◎");
                                    sourceDetail.setAwards(StringUtil.disposeField(temp[0]));
                                }
                            }
                        }
                    }
                }

            }
        }
        page.putField("sourceDetail", sourceDetail);
        page.putField("url", url);
    }
}
