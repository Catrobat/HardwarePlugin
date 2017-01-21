/**
 * Created by dominik on 21.01.17.
 */
AJS.toInit(function () {
    var file = undefined;

    AJS.$('#upload-field').change(function () {
        file = AJS.$("#upload-field").value;
    });

    AJS.$("#post-inputs").click(function () {
        console.log("about to post inputs");
        if(file === undefined) {
            AJS.messages.error({
                title: "Error !",
                body: "Select a Hardware Backup Zip-File to proceed!"
            })
        }
        else {
            if(!file.contains(".zip")){
                AJS.messages.error({
                    title: "Error !",
                    body: "The Datatype of the Backup must be .zip!"
                })
            }
            AJS.$("#upload-form").submit();
        }
    })
});
