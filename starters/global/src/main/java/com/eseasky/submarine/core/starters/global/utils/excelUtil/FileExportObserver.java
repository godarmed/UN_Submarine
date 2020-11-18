package com.eseasky.submarine.core.starters.global.utils.excelUtil;

import com.eseasky.submarine.core.starters.global.utils.excelUtil.entity.FileStatus;
import com.eseasky.submarine.core.starters.global.utils.excelUtil.entity.FileStatus;

import java.util.List;

public interface FileExportObserver {
	void update(List<FileStatus> fileStatusList);
}
