SKIP = "${@bb.utils.contains('IMAGE_FEATURES', 'update' ,'false', 'true', d)}"

do_image_swupdate() {

    if [ ${SKIP} == "false" ]; then

        bbwarn "Generating swu file for swupdate!"
        FILES="sw-description ${PN}.ext4"

        cd ${DEPLOY_DIR_IMAGE}
        for i in $FILES;do
            echo $i;done | cpio -ovL -H crc >  ${PN}.${DATETIME}.swu

        ln -sf ${PN}.${DATETIME}.swu ${PN}.swu
    else
        bbwarn "Skipping creation of ${PN}.${DATETIME}.swu!"
    fi
}
addtask do_image_swupdate after do_image before do_build
