FILESPATH_prepend := "${THISDIR}/files:"

FW_ENV_nanopi-cactus = "fw_env.config.sunxi"
SRC_URI += "\
           file://${FW_ENV} \
           "


# Build fw_setenv/fw_printenv for the target
debianize_build_append() {
	${MAKE} env
}

debianize_install_append() {
	mkdir -p debian/${BPN}/sbin
	mkdir -p debian/${BPN}/etc
	install -m 0755 ${PPS}/tools/env/fw_printenv debian/${BPN}/sbin/fw_printenv
	install -m 0755 ${PPS}/tools/env/fw_printenv debian/${BPN}/sbin/fw_setenv
	install -m 0644 ${PP}/${FW_ENV} debian/${BPN}/etc/fw_env.config
}