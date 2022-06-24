export default {
    data() {
        return {
            toast: this.$swal.mixin({
                toast: true,
                position: "top-right",
                iconColor: "white",
                customClass: {
                    popup: "colored-toast",
                },
                showConfirmButton: false,
                timer: 3000,
            }),
        };
    },
};
