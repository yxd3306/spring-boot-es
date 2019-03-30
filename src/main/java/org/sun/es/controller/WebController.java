package org.sun.es.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sun.es.dao.PoemRepository;
import org.sun.es.entity.Poem;
import org.sun.es.service.PoemServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linziyu on 2018/5/19.
 * 控制层
 */

@Controller
public class WebController {
    @Autowired
    private PoemServiceImpl poemService;

    @Autowired
    PoemRepository poemRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Poem> poems = new ArrayList<>();
        poems.add(new Poem(4, "湘春夜月·近清明", "近清明,翠禽枝上消魂,可惜一片清歌，都付与黄昏。欲共柳花低诉，怕柳花轻薄，不解伤春。念楚乡旅宿，柔情别绪，谁与温存。"));
        poems.add(new Poem(5, "卜算子·不是爱风尘", "不是爱风尘，似被前缘误。花落花开自有时，总赖东君主。\n" +
                "去也终须去，住也如何住！若得山花插满头，莫问奴归处"));
        poems.add(new Poem(6, "御街行·秋日怀旧", "纷纷坠叶飘香砌。夜寂静，寒声碎。真珠帘卷玉楼空，天淡银河垂地。年年今夜，月华如练，长是人千里。"));

        for (int i = 0; i < poems.size(); i++) {
            poemService.save(poems.get(i));
        }
        model.addAttribute("poems", poems);

        return "/index";

    }


    @ApiOperation(value = "elasticsearch全文检索入口",notes = "根据title检索数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "输入的关键字", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "pageIndex", value = "当前页", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数", required = true, dataType = "int", paramType = "query")
    })
    @PostMapping("/search")
    public String search(String content, @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                         @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,Model model) {
                Pageable pageable = PageRequest.of(pageIndex,pageSize);
                Page<Poem> poems = poemService.search(content,pageable);
                List<Poem> list = poems.getContent();
                model.addAttribute("poems",list);
                return "/list";

    }



}
