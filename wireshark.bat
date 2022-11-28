export DISPLAY=host.docker.internal:0
export LIBGL_ALWAYS_INDIRECT=1
chmod 700 /tmp/foobar
export XDG_RUNTIME_DIR=/tmp/foobar
wireshark -i eth1 -k --log-level critical &