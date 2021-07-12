package kitchenpos.menu.ui;

import java.net.URI;
import java.util.List;
import kitchenpos.menu.application.MenuGroupService;
import kitchenpos.menu.dto.MenuGroupDto;
import kitchenpos.menu.dto.dto.CreateMenuGroupRequest;
import kitchenpos.menu.dto.dto.MenuGroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
public class MenuGroupRestController {
    private final MenuGroupService menuGroupService;

    public MenuGroupRestController(final MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @PostMapping("/api/menu-groups")
    public ResponseEntity<MenuGroupResponse> create(@RequestBody CreateMenuGroupRequest request) {
        MenuGroupDto created = menuGroupService.create(request.getName());
        URI uri = URI.create("/api/menu-groups/" + created.getId());
        return ResponseEntity.created(uri)
                             .body(MenuGroupResponse.of(created));
    }

    @GetMapping("/api/menu-groups")
    public ResponseEntity<List<MenuGroupResponse>> list() {
        return ResponseEntity.ok()
                             .body(menuGroupService.list()
                                                   .stream()
                                                   .map(MenuGroupResponse::of)
                                                   .collect(toList()));
    }
}
