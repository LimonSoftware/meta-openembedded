SUMMARY = "USB CEC Adaptor communication Library"
HOMEPAGE = "http://libcec.pulse-eight.com/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b3a719e97f49e4841e90573f9b1a98ac"

DEPENDS = "p8platform udev ncurses swig-native python3"

DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxrandr', '', d)}"
DEPENDS:append:rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', '', ' userland', d)}"

SRCREV = "29d82c80bcc62be2878a9ac080de7eb286c4beb9"
SRC_URI = "git://github.com/Pulse-Eight/libcec.git;branch=release;protocol=https \
           file://0001-CheckPlatformSupport.cmake-Do-not-hardcode-lib-path.patch \
           file://0001-Enhance-reproducibility.patch \
           file://0001-Remove-buggy-test-confusing-host-and-target.patch \
           file://0001-cecloader-Match-return-type-of-function-LibCecBootlo.patch \
          "

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# default config is for RaspberryPi API, use the Linux 4.10+ API by default
PLATFORM_CMAKE_FLAGS ?= "-DHAVE_LINUX_API=1 -DHAVE_RPI_API=0 -DSKIP_PYTHON_WRAPPER=1"
EXTRA_OECMAKE += "${PLATFORM_CMAKE_FLAGS}"

# Put client examples into separate packages
PACKAGE_BEFORE_PN += "${PN}-examples-python ${PN}-examples"
FILES:${PN}-examples-python = "${bindir}/py*"
FILES:${PN}-examples = "${bindir}"
# cec-client doesn't link with libcec, but uses LibCecInitialise to dlopen libcec, so do_package
# cannot add the runtime dependency automatically
RDEPENDS:${PN}-examples = "${PN}"
RDEPENDS:${PN}-examples-python = "python3-${BPN} python3-core"

# Create the wrapper for python3
PACKAGES += "python3-${BPN}"
FILES:python3-${BPN} = "${libdir}/python3* ${bindir}/py*"
RDEPENDS:${PN} = "python3-core"

# cec-client and xbmc need the .so present to work :(
FILES:${PN} += "${libdir}/*.so"
INSANE_SKIP:${PN} = "dev-so"

# Adapter shows up as a CDC-ACM device
RRECOMMENDS:${PN} = "kernel-module-cdc-acm"
