package kitchenpos.table.ui;

import kitchenpos.table.application.TableGroupService;
import kitchenpos.table.domain.TableGroup;
import kitchenpos.table.dto.TableGroupDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class TableGroupRestController {
    private final TableGroupService tableGroupService;

    public TableGroupRestController(final TableGroupService tableGroupService) {
        this.tableGroupService = tableGroupService;
    }

    @PostMapping("/api/table-groups")
    public ResponseEntity<TableGroupDto> create(@RequestBody TableGroupDto tableGroupDto) {
        final TableGroup created = tableGroupService.create(tableGroupDto);
        final URI uri = URI.create("/api/table-groups/" + created.getId());
        return ResponseEntity.created(uri).body(TableGroupDto.of(created));
    }

    @DeleteMapping("/api/table-groups/{tableGroupId}")
    public ResponseEntity<Void> ungroup(@PathVariable final Long tableGroupId) {
        tableGroupService.ungroup(tableGroupId);
        return ResponseEntity.noContent().build();
    }
}
