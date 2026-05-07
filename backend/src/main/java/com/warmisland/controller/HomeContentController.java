package com.warmisland.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.warmisland.dto.Result;
import com.warmisland.entity.HomeSection;
import com.warmisland.entity.HomeSectionItem;
import com.warmisland.mapper.HomeSectionItemMapper;
import com.warmisland.mapper.HomeSectionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class HomeContentController {

    private final HomeSectionMapper homeSectionMapper;
    private final HomeSectionItemMapper homeSectionItemMapper;

    public HomeContentController(
            HomeSectionMapper homeSectionMapper,
            HomeSectionItemMapper homeSectionItemMapper) {
        this.homeSectionMapper = homeSectionMapper;
        this.homeSectionItemMapper = homeSectionItemMapper;
    }

    @GetMapping("/api/home")
    public Result<List<HomeSection>> publicHome() {
        return Result.success(listSections(false));
    }

    @GetMapping("/api/admin/home")
    public Result<List<HomeSection>> adminHome() {
        return Result.success(listSections(true));
    }

    @PutMapping("/api/admin/home/sections/{code}")
    public Result<HomeSection> updateSection(@PathVariable String code, @RequestBody HomeSection payload) {
        HomeSection current = homeSectionMapper.selectOne(new LambdaQueryWrapper<HomeSection>()
                .eq(HomeSection::getCode, code));

        if (current == null) {
            return Result.error(404, "首页内容位不存在");
        }

        current.setEyebrow(payload.getEyebrow());
        current.setTitle(payload.getTitle());
        current.setSubtitle(payload.getSubtitle());
        current.setBody(payload.getBody());
        current.setImageUrl(payload.getImageUrl());
        current.setLinkText(payload.getLinkText());
        current.setLinkUrl(payload.getLinkUrl());
        current.setSortOrder(payload.getSortOrder() == null ? 0 : payload.getSortOrder());
        current.setStatus(payload.getStatus() == null || payload.getStatus().isBlank() ? "启用" : payload.getStatus());
        homeSectionMapper.updateById(current);

        return Result.success(current);
    }

    @PostMapping("/api/admin/home/sections/{code}/items")
    public Result<HomeSectionItem> createItem(@PathVariable String code, @RequestBody HomeSectionItem payload) {
        HomeSection section = homeSectionMapper.selectOne(new LambdaQueryWrapper<HomeSection>()
                .eq(HomeSection::getCode, code));

        if (section == null) {
            return Result.error(404, "首页内容位不存在");
        }

        payload.setId(null);
        payload.setSectionCode(code);
        payload.setSortOrder(payload.getSortOrder() == null ? 0 : payload.getSortOrder());
        payload.setStatus(payload.getStatus() == null || payload.getStatus().isBlank() ? "启用" : payload.getStatus());
        homeSectionItemMapper.insert(payload);

        return Result.success(payload);
    }

    @PutMapping("/api/admin/home/items/{id}")
    public Result<HomeSectionItem> updateItem(@PathVariable Long id, @RequestBody HomeSectionItem payload) {
        HomeSectionItem current = homeSectionItemMapper.selectById(id);

        if (current == null) {
            return Result.error(404, "首页内容项不存在");
        }

        current.setItemType(payload.getItemType());
        current.setTitle(payload.getTitle());
        current.setSubtitle(payload.getSubtitle());
        current.setDescription(payload.getDescription());
        current.setImageUrl(payload.getImageUrl());
        current.setLinkText(payload.getLinkText());
        current.setLinkUrl(payload.getLinkUrl());
        current.setSortOrder(payload.getSortOrder() == null ? 0 : payload.getSortOrder());
        current.setStatus(payload.getStatus() == null || payload.getStatus().isBlank() ? "启用" : payload.getStatus());
        homeSectionItemMapper.updateById(current);

        return Result.success(current);
    }

    @DeleteMapping("/api/admin/home/items/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        if (homeSectionItemMapper.deleteById(id) == 0) {
            return Result.error(404, "首页内容项不存在");
        }

        return Result.success();
    }

    private List<HomeSection> listSections(boolean includeDisabled) {
        LambdaQueryWrapper<HomeSection> sectionQuery = new LambdaQueryWrapper<HomeSection>()
                .orderByAsc(HomeSection::getSortOrder)
                .orderByAsc(HomeSection::getId);

        if (!includeDisabled) {
            sectionQuery.eq(HomeSection::getStatus, "启用");
        }

        List<HomeSection> sections = homeSectionMapper.selectList(sectionQuery);

        LambdaQueryWrapper<HomeSectionItem> itemQuery = new LambdaQueryWrapper<HomeSectionItem>()
                .orderByAsc(HomeSectionItem::getSortOrder)
                .orderByAsc(HomeSectionItem::getId);

        if (!includeDisabled) {
            itemQuery.eq(HomeSectionItem::getStatus, "启用");
        }

        Map<String, List<HomeSectionItem>> itemsBySection = homeSectionItemMapper.selectList(itemQuery)
                .stream()
                .collect(Collectors.groupingBy(HomeSectionItem::getSectionCode));

        sections.forEach(section -> section.setItems(itemsBySection.getOrDefault(section.getCode(), List.of())));
        return sections;
    }
}
