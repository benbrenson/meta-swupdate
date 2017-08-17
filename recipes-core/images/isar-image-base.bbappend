IMAGE_INSTALL_append = " swupdate "

do_image_swupdate() {
    bbwarn "Generating swu file for swupdate!"
    FILES="sw-description ${PN}.ext4"

    cd ${DEPLOY_DIR_IMAGE}
    for i in $FILES;do
        echo $i;done | cpio -ovL -H crc >  ${PN}.${DATETIME}.swu

    ln -s ${PN}.${DATETIME}.swu ${PN}.swu

}
addtask do_image_swupdate after do_image before do_build