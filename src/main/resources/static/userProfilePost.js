
//Like
function attachLikeFormListeners() {
  const forms = document.querySelectorAll(".likePostForm");
  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

      const posts = {
        postId: parseInt(form.querySelector(".postId").value),
        user: { userId: parseInt(form.querySelector(".userId").value) },
      };
      const redirectPage = form.querySelector(".redirectPage").value;
      const followerId = parseInt(form.querySelector(".followerId").value);

      const requestBody = {
        posts: posts,
        redirectPage: redirectPage,
        followerId: followerId,
      };

      fetch("/like", {
        method: "POST",
        body: JSON.stringify(requestBody),
        headers: {
          "Content-Type": "application/json",
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType.includes("text/html")) {
              return response.text().then((html) => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, "text/html");
                document.body.innerHTML = doc.body.innerHTML;
                attachLikeFormListeners(); // Reattach listeners after content update
              });
            } else {
              return response.json().then((data) => {
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to like post");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while liking the post.");
        });
    });
  });
}

document.addEventListener("DOMContentLoaded", attachLikeFormListeners);

//AddComment
function attachAddCommentFormListeners() {
  const forms = document.querySelectorAll(".addCommentForm");
  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

       const postComments = {
              post: {
                postId: form.querySelector(".postId").value, // Select fields from current form
              },
              userId: form.querySelector(".userId").value,
              comment: form.querySelector("#comment").value,
            };

             const requestBody = {
                    postComments : postComments,
                    redirectPage: form.querySelector(".redirectPage").value,
                    followerId: form.querySelector(".followerId").value
                  };

      fetch("/addComment", {
        method: "POST",
        body: JSON.stringify(requestBody),
        headers: {
          "Content-Type": "application/json",
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType.includes("text/html")) {
              return response.text().then((html) => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, "text/html");
                document.body.innerHTML = doc.body.innerHTML;
                attachAddCommentFormListeners(); // Reattach listeners after content update
              });
            } else {
              return response.json().then((data) => {
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to add comment");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while adding comment.");
        });
    });
  });
}

document.addEventListener("DOMContentLoaded", attachAddCommentFormListeners);


//Delete Comment
function attachDeleteCommentFormListeners() {
  const forms = document.querySelectorAll(".deleteCommentForm");
  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

       const postComments = {
               post: {
                 postId: form.querySelector(".postId").value, // Select fields from current form
               },
               userId: form.querySelector(".userId").value,
               id: form.querySelector(".commentId").value,
             };

             const requestBody = {
                    postComments : postComments,
                    redirectPage: form.querySelector(".redirectPage").value,
                    followerId: form.querySelector(".followerId").value
                  };

      fetch("/deleteComment", {
        method: "POST",
        body: JSON.stringify(requestBody),
        headers: {
          "Content-Type": "application/json",
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType.includes("text/html")) {
              return response.text().then((html) => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, "text/html");
                document.body.innerHTML = doc.body.innerHTML;
                attachDeleteCommentFormListeners(); // Reattach listeners after content update
              });
            } else {
              return response.json().then((data) => {
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to delete comment");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while deleting comment.");
        });
    });
  });
}

document.addEventListener("DOMContentLoaded", attachDeleteCommentFormListeners);

//Edit Comment
function attachEditCommentFormListeners() {
  const forms = document.querySelectorAll(".editCommentForm");
  forms.forEach((form) => {
    form.addEventListener("submit", function (event) {
      event.preventDefault(); // Prevent default form submission

      if (!form.checkValidity()) {
        form.classList.add("was-validated");
        return;
      }

       const postComments = {
               post: {
                 postId: form.querySelector(".postId").value,
               },
               userId: form.querySelector(".userId").value,
               id: form.querySelector(".commentId").value,
               comment: form.querySelector(".comment").value,
             };

             const requestBody = {
                    postComments : postComments,
                    redirectPage: form.querySelector(".redirectPage").value,
                    followerId: form.querySelector(".followerId").value
                  };

      fetch("/editComment", {
        method: "POST",
        body: JSON.stringify(requestBody),
        headers: {
          "Content-Type": "application/json",
          Accept: "text/html",
        },
      })
        .then((response) => {
          if (response.ok) {
            const contentType = response.headers.get("content-type");
            if (contentType.includes("text/html")) {
              return response.text().then((html) => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, "text/html");
                document.body.innerHTML = doc.body.innerHTML;
                attachEditCommentFormListeners(); // Reattach listeners after content update
              });
            } else {
              return response.json().then((data) => {
                alert(data.alert);
              });
            }
          } else {
            throw new Error("Failed to editing comment");
          }
        })
        .catch((error) => {
          console.error("Error:", error);
          alert("An error occurred while editing comment.");
        });
    });
  });
}

document.addEventListener("DOMContentLoaded", attachEditCommentFormListeners);


document.addEventListener('DOMContentLoaded', () => {
            const toastLiveExample = document.getElementById('liveToast');

            if (toastLiveExample) {
                const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastLiveExample);
                toastBootstrap.show();
            }
        });

document.addEventListener("DOMContentLoaded", () => {
  // Add click event listener to all edit buttons
  document.querySelectorAll(".edit-btn").forEach((btn) => {
    btn.addEventListener("click", (event) => {
      const commentId = btn.getAttribute("data-comment-id");
      const commentBox = document.querySelector(
        `[data-comment-box-id="${commentId}"]`
      );

      // Toggle visibility of the comment box
      commentBox.classList.toggle("hidden");
    });
  });
});
