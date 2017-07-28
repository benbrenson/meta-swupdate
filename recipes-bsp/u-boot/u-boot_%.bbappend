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
	mkdir -p debian/${PN}/sbin
	mkdir -p debian/${PN}/etc
	install -m 0755 ${S}/tools/env/fw_printenv debian/${PN}/sbin/fw_printenv
	install -m 0755 ${S}/tools/env/fw_printenv debian/${PN}/sbin/fw_setenv
	install -m 0644 ${EXTRACTDIR}/${FW_ENV} debian/${PN}/etc/fw_env.config
}